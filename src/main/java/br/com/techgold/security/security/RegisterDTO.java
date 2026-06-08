package br.com.techgold.security.security;

public record RegisterDTO(
		String username,
		String password,
		String role
		) {

}
