package myvote;

/*Created by Piyush Bansal on 28 Feb 2014*/
/*Update on 29 March 2014 - addition of code for MongoDB integration*/

import java.util.ArrayList;
import java.util.Date;

import javax.validation.constraints.*;

import org.hibernate.validator.constraints.Email;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection="moderators")
public class Moderator {

	@Id 
	private int id;
	@JsonProperty("name") @NotNull
	private String name;
	@JsonProperty("email")	@Email	@NotNull
	private String email;
	@JsonProperty("password") @NotNull
	private String password;
	private String created_at;

	public Moderator() {
		// needed to create for Jackson
	}

	public Moderator(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
		// this.createdAt = new Date();
	}

	// @JsonSerialize
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return this.email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return this.password;
	}

	// @JsonSerialize(using=DateSerializer.class)
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getCreated_at() {
		return this.created_at;
	}

	public JSONObject responseModerator() {
		JSONObject responseJSON = new JSONObject();
		responseJSON.put("id", this.id);
		responseJSON.put("name", this.name);
		responseJSON.put("email", this.email);
		responseJSON.put("password", this.password);
		responseJSON.put("created_at", this.created_at);
		return responseJSON;
	}
}
