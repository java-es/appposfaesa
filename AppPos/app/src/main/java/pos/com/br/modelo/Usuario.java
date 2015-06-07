package pos.com.br.modelo;

/**
 * Created by wanderson on 06/06/2015.
 */
public class Usuario {
    private Long idUsuario;
    private String codigo;
    private String login;
    private String senha;
    private byte autenticado;

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public byte getAutenticado() {
        return autenticado;
    }

    public void setAutenticado(byte autenticado) {
        this.autenticado = autenticado;
    }
}
