SELECT EXISTS(SELECT invoice_id FROM invoice_entries WHERE invoice_id= ?);
