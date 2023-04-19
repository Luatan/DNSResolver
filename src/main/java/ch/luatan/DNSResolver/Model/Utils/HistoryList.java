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
            super.remove(0);
        }
        return super.add(domain);
    }
}
