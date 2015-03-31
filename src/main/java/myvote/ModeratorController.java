package myvote;

/*Created by Piyush Bansal on 28 Feb 2014*/

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import scala.annotation.strictfp;

import com.fasterxml.jackson.annotation.*;

//import org.springframework.beans.factory.annotation.Autowired;

@RestController
// @Configuration
// @EnableAutoConfiguration
// @ComponentScan
@RequestMapping(value = "/api/v1")
public class ModeratorController {

	@Autowired
	private ModeratorRepository moderatorRepository;
	@Autowired
	private PollRepository pollRepository;

	// HashMap<Integer, Moderator> moderatorMap = new HashMap<Integer,
	// Moderator>();
	// HashMap<String, Poll> pollMap = new HashMap<String, Poll>();
	// HashMap<Integer, ArrayList<String>> moderatorPollMap = new
	// HashMap<Integer, ArrayList<String>>();

	private final AtomicInteger moderatorIdAtomic = new AtomicInteger();
	private final AtomicLong pollId = new AtomicLong();
	TimeZone tz = TimeZone.getTimeZone("UTC");
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	// SimpleDateFormat df = new SimpleDateFormat(

	public boolean checkAuthorizationDetail(String authorizationDetail)
			throws UnsupportedEncodingException {

		try {
			if (authorizationDetail.indexOf(" ") > 0) {
				String[] authorizationDetailArray = authorizationDetail
						.split(" ");
				byte[] decodedString = Base64
						.decodeBase64(authorizationDetailArray[1]);
				String authorizationString = new String(decodedString, "UTF-8");
				if (authorizationString.indexOf(":") > 0) {
					String[] credentials = authorizationString.split(":");
					String username = credentials[0];
					String password = credentials[1];
					if (username.equals("foo") && password.equals("bar"))
						return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String validate(BindingResult bindingResult, String message) {
		String errorMessage = "";
		if (bindingResult.hasErrors()) {
			for (FieldError error : bindingResult.getFieldErrors()) {
				errorMessage += error.getField() + " - " + "is required."
						+ "; ";
			}
			return errorMessage;
		} else
			return "";
	}

	// createModerator
	@RequestMapping(value = "/moderators", method = RequestMethod.POST, headers = { "content-type=application/json" })
	@ResponseBody
	public Object createModerator(@RequestBody @Valid Moderator moderator,
			BindingResult bindingResult, HttpServletResponse httpResponse)
			throws UnsupportedEncodingException {

		System.out.println("Creating Moderator...");

		JSONObject result = new JSONObject();
		String validate = validate(bindingResult,
				"Error creating moderator: \n");
		if (validate.equals("")) {
			int moderatorId = moderatorIdAtomic.incrementAndGet();
			moderator.setId(moderatorId);
			df.setTimeZone(tz);
			moderator.setCreated_at(df.format(new Date()));
			moderatorRepository.save(moderator); // this stores moderator inside
													// moderators collection
			System.out.println("Moderator with Id " + moderatorIdAtomic
					+ " created successfully.");
			// moderatorMap.put(moderatorId, moderator);
			result = moderator.responseModerator();
			httpResponse.setStatus(HttpServletResponse.SC_CREATED);

		} else {
			httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			result.put("error", validate);
		}
		return result;
	}

	// getModerator
	@RequestMapping(value = "/moderators/{id}", method = RequestMethod.GET, headers = { "accept=application/json" })
	public @ResponseBody
	Moderator getModerator(@PathVariable("id") Integer moderatorId,
			@RequestHeader(value = "Authorization") String authorizationDetail,
			HttpServletResponse httpResponse)
			throws UnsupportedEncodingException {

		System.out.println("Searching Moderator " + moderatorId);
		boolean authenticationSuccess = checkAuthorizationDetail(authorizationDetail);
		if (authenticationSuccess) {

			Moderator moderator = moderatorRepository.findById(moderatorId); // finds
																				// the
																				// moderator
																				// with
																				// id(moderatorId)
																				// in
																				// moderator
																				// collections
			if (moderator != null) {
				System.out.println("Moderator with Id " + moderatorId
						+ " found.");
				httpResponse.setStatus(HttpServletResponse.SC_OK);
				return moderator;
			}
		} else {
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		return null;
	}

	// updateModerator
	@RequestMapping(value = "/moderators/{id}", method = RequestMethod.PUT, headers = { "content-type=application/json" })
	public JSONObject updateModerator(@PathVariable("id") Integer moderatorId,
			@RequestBody Moderator newModerator,
			// BindingResult bindingResult,
			@RequestHeader(value = "Authorization") String authorizationDetail,
			HttpServletResponse httpResponse)
			throws UnsupportedEncodingException {

		System.out.println("Updating Moderator " + moderatorId);
		JSONObject result = new JSONObject();
		boolean authenticationSuccess = checkAuthorizationDetail(authorizationDetail);
		if (authenticationSuccess) {
			// String validate = validate(bindingResult,
			// "Error Updating moderator: \n");
			// if (validate.equals("")) {
			Moderator moderator = moderatorRepository.findById(moderatorId);
			if (moderator != null) {
				if (newModerator.getName() != null)
					moderator.setName(newModerator.getName());
				if (newModerator.getEmail() != null)
					moderator.setEmail(newModerator.getEmail());
				if (newModerator.getPassword() != null)
					moderator.setPassword(newModerator.getPassword());
				moderatorRepository.save(moderator);
				result = moderator.responseModerator();
				System.out.println("Moderator with Id " + moderatorId
						+ " updated successfully.");
				httpResponse.setStatus(HttpServletResponse.SC_CREATED);
			} else {
				result.put("Message", "No moderator found!");
			}
			// } else {
			// httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			// result.put("error", validate);
			// }
		} else {
			result.put("error", "Invalid username and Password");
		}
		return result;
	}

	// deleteModerator
	@RequestMapping(value = "/moderators/{id}", method = RequestMethod.DELETE)
	public void deleteModerator(@PathVariable("id") Integer moderatorId) {
		System.out.println("Deleting Moderator " + moderatorId);
		Moderator moderator = moderatorRepository.findById(moderatorId);
		if (moderator != null) {
			moderatorRepository.delete(moderator);
			System.out
					.println("Moderator with Id " + moderatorId + " Deleted.");
		}
	}

	// createPoll
	@RequestMapping(value = "/moderators/{id}/polls", method = RequestMethod.POST, headers = { "content-type=application/json" })
	public JSONObject createPoll(@RequestBody @Valid Poll poll,
			@PathVariable("id") Integer moderatorId, BindingResult bindingResult,
			@RequestHeader(value = "Authorization") String authorizationDetail,
			HttpServletResponse httpResponse)
			throws UnsupportedEncodingException {

		System.out.println("Creating Poll.");
		JSONObject result = new JSONObject();
		boolean authenticationSuccess = checkAuthorizationDetail(authorizationDetail);
		if (authenticationSuccess) {
			String validate = validate(bindingResult, "Error creating Poll: \n");
			if (validate.equals("")) {
				Moderator moderator = moderatorRepository.findById(moderatorId);
				if (moderator != null) {
					double randomNumber = Math.random();
					String poll_id = Long.toHexString((long) (randomNumber * 123456) + (long) pollId.incrementAndGet() );
					poll.setId(poll_id);
					int choiceLength = poll.getChoice().length;
					int[] tempResult = new int[choiceLength];
					poll.setResults(tempResult);
					poll.setModeratorId(moderatorId);
					pollRepository.save(poll);
					result = poll.responsePoll();
					System.out.println("Poll with Poll Id " + pollId
							+ " successfully created.");
					httpResponse.setStatus(HttpServletResponse.SC_CREATED);
				}
			} else {
				httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				result.put("error", validate);
			}
		} else {
			result.put("error", "Invalid username and Password");
		}
		return result;
	}

	// view Poll without result
	@JsonView(View.PollWithoutResult.class)
	@RequestMapping(value = "/polls/{poll_id}", method = RequestMethod.GET, headers = "accept=application/json")
	public @ResponseBody
	Poll viewPollWithoutResult(@PathVariable("poll_id") String pollId) {
		System.out.println("Searching Poll "+ pollId + " without result.");
		Poll poll = pollRepository.findById(pollId);
		if (poll != null)
			return poll;
		return null;
	}

	// view Poll with result
	@JsonView(View.PollWithResult.class)
	@RequestMapping(value = "/moderators/{moderator_id}/polls/{poll_id}", method = RequestMethod.GET, headers = "content-type=application/json")
	public Poll viewPollWithResult(@PathVariable("moderator_id") Integer moderatorId,
			@PathVariable("poll_id") String pollId,
			@RequestHeader(value = "Authorization") String authorizationDetail,
			HttpServletResponse httpResponse)
			throws UnsupportedEncodingException {
		System.out.println("Searching Poll "+ pollId + " for " + moderatorId + " with result.");
		boolean authenticationSuccess = checkAuthorizationDetail(authorizationDetail);
		if (authenticationSuccess) {
			Poll poll = pollRepository.findById(pollId);
			if (poll != null && (poll.getModeratorId() == moderatorId)) {
				System.out.println("Poll: " + poll);
				httpResponse.setStatus(HttpServletResponse.SC_OK);
				return poll;
			}
		}

		return null;
	}

	// view all Polls of moderator
	@JsonView(View.PollWithResult.class)
	@RequestMapping(value = "/moderators/{moderator_id}/polls", method = RequestMethod.GET, headers = "content-type=application/json")
	public ArrayList<Poll> viewAllPoll(@PathVariable("moderator_id") Integer moderatorId,
			@RequestHeader(value = "Authorization") String authorizationDetail,
			HttpServletResponse httpResponse)
			throws UnsupportedEncodingException {

		System.out.println("Searching all Polls of Moderator " + moderatorId);
		boolean authenticationSuccess = checkAuthorizationDetail(authorizationDetail);
		if (authenticationSuccess) {
			ArrayList<Poll> pollList = pollRepository
					.findByModeratorId(moderatorId);
			if (pollList.size() > 0) {
				httpResponse.setStatus(HttpServletResponse.SC_OK);
				return pollList;
			}
		}
		return null;

	}

	// deletePoll
	@RequestMapping(value = "/moderators/{moderator_id}/polls/{poll_id}", method = RequestMethod.DELETE)
	public void deletePoll(@PathVariable("moderator_id") Integer moderatorId,
			@PathVariable("poll_id") String pollId,
			@RequestHeader(value = "Authorization") String authorizationDetail,
			HttpServletResponse httpResponse)
			throws UnsupportedEncodingException {

		System.out.println("Deleting a poll with pollId " + pollId);
		boolean authenticationSuccess = checkAuthorizationDetail(authorizationDetail);
		if (authenticationSuccess) {
			Poll poll = pollRepository.findById(pollId);
			if (poll != null && (poll.getModeratorId() == moderatorId)) {
				pollRepository.delete(poll);
				httpResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
			}

		}
	}

	// votePoll
	@RequestMapping(value = "/polls/{poll_id}", method = RequestMethod.PUT)
	public void votePoll(
			@RequestParam(value = "choice", required = true) Integer choice,
			@PathVariable("poll_id") String poll_id, HttpServletResponse httpResponse)
			throws UnsupportedEncodingException {

		System.out.println("Voting a Poll " + poll_id);
		if (pollRepository.exists(poll_id)) {
			Poll poll = pollRepository.findById(poll_id);
			poll.getResults()[choice]++;
			pollRepository.save(poll);
			httpResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
		}
	}

}
