export const VAT_RATES = [0.27, 0.18, 0.05] as const;

export type VatRate = (typeof VAT_RATES)[number];
