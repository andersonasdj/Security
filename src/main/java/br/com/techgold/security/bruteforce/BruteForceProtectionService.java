package br.com.techgold.security.bruteforce;


public interface BruteForceProtectionService {

    int registerLoginFailure(final String username);

    void resetErroSenhaContador(final String username);

    boolean isBruteForceAttack(final String username);
}
