-- Migration V2: Ajout des champs QR codes UEMOA à la table liquidations
-- Date: 2025-08-28
-- Description: Ajoute les champs nécessaires pour stocker les données QR codes UEMOA

-- Ajout des colonnes pour les données QR codes
ALTER TABLE liquidations 
ADD COLUMN qr_code_data TEXT,
ADD COLUMN qr_image_base64 TEXT,
ADD COLUMN merchant_channel VARCHAR(64),
ADD COLUMN transaction_id VARCHAR(128),
ADD COLUMN qr_type VARCHAR(16),
ADD COLUMN qr_generated_at TIMESTAMP,
ADD COLUMN penalty_amount DECIMAL(18,2),
ADD COLUMN total_amount DECIMAL(18,2);

-- Ajout d'index pour optimiser les requêtes
CREATE INDEX idx_liquidations_qr_type ON liquidations(qr_type);
CREATE INDEX idx_liquidations_transaction_id ON liquidations(transaction_id);
CREATE INDEX idx_liquidations_qr_generated_at ON liquidations(qr_generated_at);
CREATE INDEX idx_liquidations_merchant_channel ON liquidations(merchant_channel);

-- Ajout de contraintes de validation
ALTER TABLE liquidations 
ADD CONSTRAINT chk_qr_type CHECK (qr_type IN ('STATIC', 'DYNAMIC', 'P2P', 'PENALTY')),
ADD CONSTRAINT chk_penalty_amount CHECK (penalty_amount IS NULL OR penalty_amount >= 0),
ADD CONSTRAINT chk_total_amount CHECK (total_amount IS NULL OR total_amount >= 0);

-- Commentaires sur les colonnes pour la documentation
COMMENT ON COLUMN liquidations.qr_code_data IS 'Données du QR code généré (format EMVCo/BCEAO)';
COMMENT ON COLUMN liquidations.qr_image_base64 IS 'Image QR code encodée en Base64';
COMMENT ON COLUMN liquidations.merchant_channel IS 'Canal marchand UEMOA (ex: "int.bceao.pi")';
COMMENT ON COLUMN liquidations.transaction_id IS 'Identifiant de transaction unique';
COMMENT ON COLUMN liquidations.qr_type IS 'Type de QR code généré (STATIC, DYNAMIC, P2P, PENALTY)';
COMMENT ON COLUMN liquidations.qr_generated_at IS 'Date de génération du QR code';
COMMENT ON COLUMN liquidations.penalty_amount IS 'Montant des pénalités (si applicable)';
COMMENT ON COLUMN liquidations.total_amount IS 'Montant total (base + pénalités)';
