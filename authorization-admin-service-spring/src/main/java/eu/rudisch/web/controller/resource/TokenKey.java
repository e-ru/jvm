package eu.rudisch.web.controller.resource;

import lombok.Data;

@Data
public class TokenKey {
	private String alg;
	private String value;
}
