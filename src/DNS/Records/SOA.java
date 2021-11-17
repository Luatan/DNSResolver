package DNS.Records;

import Utils.Domain;

public class SOA extends Record {

    public SOA(String value) {
        super("SOA");
        super.setValue(format(value));
    }

    private String format(String value) {
        int max = 0;
        int padding = 2;
        String[] new_list = value.split(" ");

        for (String elem : new_list) {
            if (elem.length() > max) {
                max = elem.length();
            }
        }
        String formatting = "%-" + (max + padding) + "." + (max + padding) + "s" + "%s%n" + "\t";
        String res = "";
        res += new_list[0] + "\n\t";
        res += new_list[1].replaceFirst("[.]", "@") + "\n\t";
        res += String.format(formatting, new_list[2], "serialnumber");
        res += String.format(formatting, new_list[3], "refresh (" + Domain.getTimeFromSeconds(Integer.parseInt(new_list[3])) + ")");
        res += String.format(formatting, new_list[4], "retry (" + Domain.getTimeFromSeconds(Integer.parseInt(new_list[4])) + ")");
        res += String.format(formatting, new_list[5], "expire (" + Domain.getTimeFromSeconds(Integer.parseInt(new_list[5])) + ")");
        res += String.format(formatting, new_list[6], "minimum (" + Domain.getTimeFromSeconds(Integer.parseInt(new_list[6])) + ")");

        return res;
    }
}
