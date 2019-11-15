DELETE FROM invoice i where i.id = ?;
DELETE FROM invoice_entry  WHERE id in  (SELECT ies.entries_id FROM invoice_entries ies
       WHERE ies.invoice_id = ?);
