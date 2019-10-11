SELECT *FROM invoice_entry i
JOIN invoice_entries e
ON e.entries_id=i.id
WHERE e.invoice_id= ?
