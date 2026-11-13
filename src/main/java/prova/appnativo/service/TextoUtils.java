package prova.appnativo.service;

final class TextoUtils {

    private TextoUtils() {
    }

    static String somenteDigitos(String valor) {
        return valor == null ? null : valor.replaceAll("\\D", "");
    }

    static String limpar(String valor) {
        if (valor == null) return null;
        String texto = valor.trim();
        return texto.isEmpty() ? null : texto;
    }

    static boolean contemIgnorandoCaixa(String texto, String busca) {
        return texto != null && texto.toLowerCase().contains(busca.toLowerCase());
    }
}
