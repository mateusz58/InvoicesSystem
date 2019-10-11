CREATE OR REPLACE FUNCTION trigger_same_company_insert()
    RETURNS trigger AS
$func$
BEGIN
    IF new.buyer_id=new.seller_id then
        RAISE EXCEPTION 'Error you cannot insert with the same id for seller and buyer';
    end if;
    RETURN new;
END
$func$  LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION trigger_greater_or_equal_due_date()
    RETURNS trigger AS
$func$
BEGIN
    IF new.due_date<=new.issued_date OR new.due_date=new.issued_date then
        RAISE EXCEPTION 'Error you cannot insert same value or lower value for due_date';
    end if;
    RETURN new;
END
$func$  LANGUAGE plpgsql;

CREATE  TRIGGER  same_company BEFORE INSERT OR UPDATE ON invoice
    FOR EACH ROW
EXECUTE PROCEDURE trigger_same_company_insert();

CREATE TRIGGER greater_or_equal_due_date BEFORE INSERT OR UPDATE ON invoice
    FOR EACH ROW
EXECUTE PROCEDURE trigger_greater_or_equal_due_date();
