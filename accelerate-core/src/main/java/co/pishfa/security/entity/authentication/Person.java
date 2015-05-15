package co.pishfa.security.entity.authentication;

import co.pishfa.security.entity.authorization.BaseSecuredEntity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 
 * @author Taha Ghasemi
 * 
 * 
 */
@Entity
@Table(name = "ac_person")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER)
@Cacheable
@DiscriminatorValue("1")
public class Person extends BaseSecuredEntity {

	private static final long serialVersionUID = 1L;

	public Person() {
	}

	@Size(max = 200)
	private String fname;

	@Size(max = 200)
	private String lname;

	@Transient
	private String title;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "person")
    private List<User> users;

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	@Override
	public String getTitle() {
		if (title == null) {
			if (fname != null) {
				title = fname;
			}
			if (lname != null) {
				if (title == null)
					title = lname;
				else
					title += " " + lname;
			}
		}
		return title;
	}

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
