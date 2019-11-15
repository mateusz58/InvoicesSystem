SELECT O.*,
       A.account_number as buyer_account_number, A.address as buyer_address,A.email as buyer_email,A.name as buyer_name,A.phone_number as buyer_phone_number,A.tax_id as buyer_tax_id,
       B.account_number as seller_account_number,B.address as seller_address,B.email as seller_email,B.name as seller_name,B.phone_number as seller_phone_number,B.tax_id as seller_tax_id
FROM INVOICE O
         JOIN COMPANY A ON (O.buyer_id=A.id)
         JOIN COMPANY B ON(O.seller_id=B.id)
ORDER BY O.id asc
