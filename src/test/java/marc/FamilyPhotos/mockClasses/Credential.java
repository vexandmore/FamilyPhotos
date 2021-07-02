package marc.FamilyPhotos.mockClasses;


/**
 * Credential for test login purposes.
 * @author Marc
 */
public class Credential {
	private String username, password;
	
	public Credential(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
