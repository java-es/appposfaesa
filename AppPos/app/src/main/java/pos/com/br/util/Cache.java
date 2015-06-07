package pos.com.br.util;

import pos.com.br.modelo.Usuario;

/**
 * Created by wanderson on 06/06/2015.
 */
public class Cache {

    private static Usuario usuario;

    public static Usuario getUsuario() {
        return usuario;
    }

    public static void setUsuario(Usuario usuario) {
        Cache.usuario = usuario;
    }
}
