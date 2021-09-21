package lessons.lesson_10_spring_security.controllers.finances.accounts;

import java.util.ArrayList;
import java.util.List;

public class AccountsListDto {
    private List<AccountResponse> accounts = new ArrayList<>();

    public AccountsListDto() {}

    public void addAccount(AccountResponse accountResponse) {
        accounts.add(accountResponse);
    }

    public List<AccountResponse> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountResponse> accounts) {
        this.accounts = accounts;
    }
}
