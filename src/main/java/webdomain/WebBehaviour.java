package webdomain;

public class WebBehaviour{
	
	private String name;
	private String enumName;

	public WebBehaviour(String name, String enumName){
		this.name = name;
		this.enumName = enumName;
	}

	public String getName(){
		return this.name;
	}
	public String getEnumName(){
		return this.enumName;
	}


}