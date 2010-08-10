package ar.com.infocompany.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.infocompany.infraestructure.BusinessBase;
import ar.com.infocompany.infraestructure.IAggregateRoot;

public class Critic extends BusinessBase implements IAggregateRoot{
	
	public static final CriticComparator comparator = new CriticComparator();
	
	private User author;
	private int salary;
	private List<Comment> comments;
	private List<Item> items;
	private int postiveScore;
	private int negativeScore;
	private Location location;
	private String industryName;
	private String jobName;
	
	@SuppressWarnings("unused")
	private int version; //To concurrency control in Hibernate
	
	public Critic() {
		
	}
		
	public Critic(User author, Comment comment, String jobName, String industryName, int salary) {
		this.author = author;
		this.salary = salary;
		this.jobName = jobName;
		this.industryName = industryName;
		this.postiveScore = 0;
		this.negativeScore = 0;
		this.items = new ArrayList<Item>();
		this.comments = new ArrayList<Comment>();
		this.comments.add(comment);
	}
	
	public Critic(User author, Comment comment, Job job, int salary) {
		this(author, comment, job.getName(), job.getIndustry().getName() , salary);
	}

	public Critic(User author, Comment comment,
			Job job, int salary, List<Item> items) {
		this(author, comment, job, salary);
		this.items = items;
	}
	
	public float getScore() {
		float score = 0;
		for (Item item : this.items) {
			score += item.getScore(); 
		}
		score /= this.items.size();
		return score;
	}
	
	public void increaseCriticScore() {
		++this.postiveScore;
	}
	
	public void decreaseCriticScore() {
		++this.negativeScore;
	}

	public int getCriticScore() {
		return (this.getPositiveCriticScore() - this.getNegativeCriticScore());
	}

	public int getPositiveCriticScore() {
		return this.postiveScore;
	}
	
	public int getNegativeCriticScore() {
		return this.negativeScore;
	}
	
	public User getAuthor() {
		return this.author;
	}
	
	public Date getDate() {
		return this.getAuthorComment().getDate();
	}
	
	public Job getJob() {
		
		return Job.getJob(industryName, jobName);
	}
	
	public Comment getAuthorComment() {
		return this.comments.get(0);
	}
	
	public int getSalary() {
		return this.salary;
	}
	
	public List<Item> getItems() {
		return this.items;
	}
	
	public List<Comment> getReplies() {
		List<Comment> replies = this.comments.subList( 1, comments.size() ); 
		return replies;
	}
	
	public boolean addReply(Comment reply) {
		return this.comments.add(reply);
	}
		
	public boolean addItem(Item criticItem) {
		return this.items.add(criticItem);
	}

	protected void validate() {	
		if(this.isVoid(industryName) || this.isVoid(jobName) ) {
			this.addBrokenRule("Job", "El trabajo es requerido.");
		}
		
		if (this.salary > 0) {
			this.addBrokenRule("Salary", "El sueldo tiene que ser mayor que 0.");
		}
		
		if (this.author == null) {
			this.addBrokenRule("Author", "El autor del la critica es requerido.");
		}
		
		if (this.items == null) {
			this.addBrokenRule("Items", "Una critica debe tener al menos un item calificado");
		} else if (this.items.size() == 0 ) {
			this.addBrokenRule("Items", "Una critica debe tener al menos un item calificado");
		} else {
			for (Item item : this.items) {
				this.addBrokenRule(item.getBrokenRules());
			}
		}
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}
		
}

// eof
