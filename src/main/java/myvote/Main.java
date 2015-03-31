package myvote;
/*Created by Piyush Bansal on 28 Feb 2014*/

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main{

/*	@RequestMapping("/")
	String Home(){
		return "Hello Piyush!";
	}
*/
	public static void main(String[] args) throws Exception{
		SpringApplication.run(Main.class, args);
	}
}