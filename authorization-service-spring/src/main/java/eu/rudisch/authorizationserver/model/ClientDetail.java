package eu.rudisch.authorizationserver.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "oauth_client_details")
@Data
public class ClientDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "client_id")
	private String clientId;
	@Column(name = "client_secret")
	private String clientSecret;
	@Column(name = "web_server_redirect_uri")
	private String webServerRedirectUri;
	@Column(name = "scope")
	private String scope;
	@Column(name = "access_token_validity")
	private String accessTokenValidity;
	@Column(name = "refresh_token_validity")
	private String refreshTokenValidity;
	@Column(name = "resource_ids")
	private String resourceIds;
	@Column(name = "authorized_grant_types")
	private String authorizationGrantTypes;
	@Column(name = "authorities")
	private String authorities;
	@Column(name = "additional_information")
	private String additionalInformation;
	@Column(name = "autoapprove")
	private String autoApprove;

}
