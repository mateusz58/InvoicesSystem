import {Invoice} from "./invoice";

interface User {
    invoices?: Invoice[];
    isLoggedIn?: boolean | false;
    isAdministrator?: boolean | false;
}