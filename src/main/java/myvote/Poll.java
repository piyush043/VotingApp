package myvote;	

/*Created by Piyush Bansal on 28 Feb 2014*/

import java.util.Date;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection= "polls")
public class Poll{
	
	@Id @JsonView(View.PollWithoutResult.class)
	private	String id;
	
	@NotNull (message="Question missing or wrong.")
	@JsonView(View.PollWithoutResult.class)
	private String question;
	
	@JsonView(View.PollWithoutResult.class)
	@NotNull(message="started date missing or wrong.")
	private String started_at;
	
	@JsonView(View.PollWithoutResult.class)
	@NotNull(message="expired date missing or wrong.")
	private String expired_at;
	
	@JsonView(View.PollWithoutResult.class)
	@NotNull(message="Choice missing or wrong.")
	private String[] choice;
	
	@JsonView(View.PollWithResult.class)
	int[] results;
	
	@JsonIgnore
	private int moderatorId;

	public Poll(){
		//created for jackson
	}
	public void setId(String id){
		this.id = id;
	}
	public String getId(){
		return id;
	}
	public void setQuestion(String question){
		this.question = question;
	}
	public String getQuestion(){
		return question;
	}
	public void setStarted_at(String started_at){
		this.started_at = started_at;
	}
	public String getStartedAt(){
		return started_at;
	}
	public void setExpired_at(String expired_at){
		this.expired_at = expired_at;
	}
	public String getExpiredAt(){
		return expired_at;
	}
	public void setChoice(String[] choice){
		this.choice = choice;
	}
	public String[] getChoice(){
		return choice;
	}
	public void setResults(int[] results){
		this.results = results;
	}
	public int[] getResults(){
		return results;
	}
	
	public int getModeratorId() {
		return moderatorId;
	}
	public void setModeratorId(int moderatorId) {
		this.moderatorId = moderatorId;
	}
	
	public JSONObject responsePoll(){
		JSONObject responseJSON = new JSONObject();
		responseJSON.put("id", this.id);
		responseJSON.put("question", this.question);
		responseJSON.put("started_at", this.started_at);
		responseJSON.put("expired_at", this.expired_at);
		responseJSON.put("choice", this.choice);
	
		return responseJSON;
	}
}
