package com.example.demoQrcode.service;

import com.aveplus.uemoa.qr.model.MerchantInfo;
import com.aveplus.uemoa.qr.model.QRPaymentData;
import com.aveplus.uemoa.qr.service.UemoaQRService;
import com.example.demoQrcode.config.UemoaConfig;
import com.example.demoQrcode.dto.UemoaQRWorkflowRequest;
import com.example.demoQrcode.dto.UemoaQRWorkflowResponse;
import com.example.demoQrcode.dto.ClientInfoResponse;
import com.example.demoQrcode.entity.Customer;
import com.example.demoQrcode.entity.Liquidation;
import com.example.demoQrcode.repository.CustomerRepository;
import com.example.demoQrcode.repository.LiquidationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

/**
 * Service principal pour le workflow UEMOA QR Code
 * Implémente le workflow : QR → Scan → Link → API → Client Info
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UemoaWorkflowService {
    
    private final UemoaQRService uemoaQRService;
    private final UemoaConfig uemoaConfig;
    private final CustomerRepository customerRepository;
    private final LiquidationRepository liquidationRepository;
    
    /**
     * Génère un QR code UEMOA et prépare le workflow complet
     * 
     * @param request Requête de génération
     * @return Réponse avec QR code et lien généré
     */
    public UemoaQRWorkflowResponse generateQRCodeAndWorkflow(UemoaQRWorkflowRequest request) {
        try {
            log.info("Début de génération du workflow UEMOA pour le montant: {} centimes", request.getAmount());
            
            // Validation du montant
            validateAmount(request.getAmount());
            
            // Utilisation du nom du marchand fourni ou de celui par défaut
            String merchantName = Optional.ofNullable(request.getMerchantName())
                    .filter(name -> !name.trim().isEmpty())
                    .orElse(uemoaConfig.getMerchantName());
            
            // Création des informations du marchand
            MerchantInfo merchantInfo = MerchantInfo.builder()
                    .alias(uemoaConfig.getTest().getMerchantId())
                    .name(merchantName)
                    .city(uemoaConfig.getMerchantCity())
                    .countryCode(uemoaConfig.getCountryCode())
                    .build();
            
            // Création des données de paiement selon le type
            QRPaymentData paymentData;
            String qrType = request.getQrType().toUpperCase();
            
            switch (qrType) {
                case "STATIC":
                    paymentData = createStaticQRData(request, merchantInfo);
                    break;
                case "DYNAMIC":
                    paymentData = createDynamicQRData(request, merchantInfo);
                    break;
                default:
                    throw new IllegalArgumentException("Type de QR code non supporté: " + qrType);
            }
            
            // Génération du QR code via le module UEMOA
            String qrData = uemoaQRService.generateQRData(paymentData);
            
            // Génération de l'image QR
            String qrImage = uemoaQRService.generateQRImage(paymentData);
            
            // Génération du lien avec informations client encodées
            String generatedLink = generateClientInfoLink(request.getClientInfo(), request.getAmount());
            
            // Construction des données du QR code
            UemoaQRWorkflowResponse.QRCodeData qrCodeData = UemoaQRWorkflowResponse.QRCodeData.builder()
                    .qrData(qrData)
                    .qrImage(qrImage)
                    .qrType(qrType)
                    .amount(request.getAmount())
                    .currency(uemoaConfig.getCurrency())
                    .merchantName(merchantName)
                    .merchantCity(uemoaConfig.getMerchantCity())
                    .countryCode(uemoaConfig.getCountryCode())
                    .transactionReference(request.getTransactionReference())
                    .generatedAt(LocalDateTime.now())
                    .build();
            
            log.info("Workflow UEMOA généré avec succès pour: {}, lien: {}", merchantName, generatedLink);
            
            return UemoaQRWorkflowResponse.success(
                    "QR code UEMOA généré avec succès et workflow préparé",
                    qrCodeData,
                    generatedLink
            );
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du workflow UEMOA: {}", e.getMessage(), e);
            return UemoaQRWorkflowResponse.error("Erreur lors de la génération: " + e.getMessage());
        }
    }
    
    /**
     * Récupère les informations client à partir du lien généré
     * 
     * @param encodedClientInfo Informations client encodées
     * @return Réponse avec informations client
     */
    public ClientInfoResponse getClientInfoFromLink(String encodedClientInfo) {
        try {
            log.info("Récupération des informations client depuis le lien");
            
            // Décodage des informations client
            String decodedClientInfo = new String(Base64.getDecoder().decode(encodedClientInfo));
            
            // Parsing des informations client (format: clientId:amount:timestamp)
            String[] parts = decodedClientInfo.split(":");
            if (parts.length < 3) {
                throw new IllegalArgumentException("Format des informations client invalide");
            }
            
            String clientId = parts[0];
            BigDecimal amount = new BigDecimal(parts[1]);
            LocalDateTime timestamp = LocalDateTime.parse(parts[2]);
            
            // Récupération des informations du client depuis la base de données
            Optional<Customer> customerOpt = customerRepository.findById(Long.parseLong(clientId));
            if (customerOpt.isEmpty()) {
                return ClientInfoResponse.error("Client non trouvé avec l'ID: " + clientId);
            }
            
            Customer customer = customerOpt.get();
            
            // Récupération des informations de liquidation si disponible
            Optional<Liquidation> liquidationOpt = liquidationRepository.findByCustomerId(customer.getId())
                    .stream()
                    .filter(l -> l.getAmount().equals(amount))
                    .findFirst();
            
            // Construction des données client
            ClientInfoResponse.ClientData clientData = ClientInfoResponse.ClientData.builder()
                    .clientId(clientId)
                    .name(customer.getLastName())
                    .firstName(customer.getFirstName())
                    .email(customer.getEmail())
                    .phone(customer.getPhone())
                    .address(customer.getAddress())
                    .city("Abidjan") // Valeur par défaut
                    .postalCode("225") // Valeur par défaut
                    .country("CI") // Valeur par défaut
                    .clientType("PARTICULIER") // Valeur par défaut
                    .createdAt(LocalDateTime.now()) // Valeur par défaut
                    .build();
            
            // Construction des données de transaction
            ClientInfoResponse.TransactionData transactionData = ClientInfoResponse.TransactionData.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .amount(amount)
                    .currency(uemoaConfig.getCurrency())
                    .status("PENDING")
                    .type("UEMOA_QR_PAYMENT")
                    .description("Paiement via QR code UEMOA")
                    .timestamp(timestamp)
                    .paymentReference("UEMOA-" + System.currentTimeMillis())
                    .build();
            
            log.info("Informations client récupérées avec succès pour: {}", customer.getLastName());
            
            return ClientInfoResponse.success(
                    "Informations client récupérées avec succès",
                    clientData,
                    transactionData
            );
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des informations client: {}", e.getMessage(), e);
            return ClientInfoResponse.error("Erreur lors de la récupération: " + e.getMessage());
        }
    }
    
    /**
     * Valide le montant selon les règles UEMOA
     */
    private void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.valueOf(uemoaConfig.getAmount().getMinAmount())) < 0) {
            throw new IllegalArgumentException("Le montant est trop faible (minimum: " + 
                    uemoaConfig.getAmount().getMinAmount() + " centimes)");
        }
        if (amount.compareTo(BigDecimal.valueOf(uemoaConfig.getAmount().getMaxAmount())) > 0) {
            throw new IllegalArgumentException("Le montant est trop élevé (maximum: " + 
                    uemoaConfig.getAmount().getMaxAmount() + " centimes)");
        }
    }
    
    /**
     * Crée les données pour un QR code statique
     */
    private QRPaymentData createStaticQRData(UemoaQRWorkflowRequest request, MerchantInfo merchantInfo) {
        return QRPaymentData.builder()
                .type(QRPaymentData.QRType.STATIC)
                .merchantInfo(merchantInfo)
                .amount(request.getAmount())
                .merchantChannel(QRPaymentData.MerchantChannel.STATIC_WITH_AMOUNT)
                .build();
    }
    
    /**
     * Crée les données pour un QR code dynamique
     */
    private QRPaymentData createDynamicQRData(UemoaQRWorkflowRequest request, MerchantInfo merchantInfo) {
        String transactionRef = Optional.ofNullable(request.getTransactionReference())
                .orElse("TXN-" + System.currentTimeMillis());
        
        return QRPaymentData.builder()
                .type(QRPaymentData.QRType.DYNAMIC)
                .merchantInfo(merchantInfo)
                .amount(request.getAmount())
                .transactionId(transactionRef)
                .merchantChannel(QRPaymentData.MerchantChannel.DYNAMIC_ONSITE)
                .build();
    }
    
    /**
     * Génère le lien avec informations client encodées
     */
    private String generateClientInfoLink(String clientInfo, BigDecimal amount) {
        // Format: clientId:amount:timestamp
        String clientData = clientInfo + ":" + amount + ":" + LocalDateTime.now();
        String encodedData = Base64.getEncoder().encodeToString(clientData.getBytes());
        
        // Construction de l'URL complète qui déclenchera l'API
        return "/api/uemoa-workflow/client-info/" + encodedData;
    }
}
