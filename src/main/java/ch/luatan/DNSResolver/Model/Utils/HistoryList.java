package ch.luatan.DNSResolver.Model.Utils;

import java.util.ArrayList;

public class HistoryList<E> extends ArrayList<E> {
    @Override
    public boolean add(E domain) {
        if (domain.equals("")) {
            return false;
        }

        if (this.contains(domain)) {
            super.remove(domain);
        }

        if (this.size() > 9) {
            super.remove(super.stream().findFirst().get());
        }
        return super.add(domain);
    }
}
