/*
 * Company
 * 
 */
package ar.com.infocompany.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ar.com.infocompany.infraestructure.BusinessBase;
import ar.com.infocompany.infraestructure.IAggregateRoot;

public class Company extends BusinessBase implements IAggregateRoot {
	
	public static final String NAME = "name";
	
	private String name;
	private Industry industry;
	private List<Critic> critics;
	
	@SuppressWarnings("unused")
	private int version; //To concurrency control in Hibernate
	
	public Company() {
		 
	}
	
	public Company(String name, Industry industry) {
		this.name = name;
		this.industry = industry;
		this.critics = new ArrayList<Critic>();
	}
	
	public float getRating() {
		float rating = 0;
		for (Critic critic : this.critics) {
			rating += critic.getScore();
		}
		rating /= this.critics.size();
		return rating;
	}
	
	public List<Critic> getBestCritics(int n) {
		return this.getCritics(n, 1);
	}
	
	public List<Critic> getWorstCritics(int n) {
		return this.getCritics(n, 0);
	}
	
	public Critic getLastCritic() {
		Critic lastCritic = null;
		if (this.critics.size() > 0) {
			lastCritic = this.critics.get(0);
			for (Critic critic : this.critics) {
				if (critic.getDate().before(lastCritic.getDate())) {
					lastCritic = critic;
				}
			}
		}
		return lastCritic;
	}
		
	public void removeCritic(Critic critic) {
		this.critics.remove(critic);
	}
	
	public boolean addCritic(Critic critic) {
		return this.critics.add(critic);
	}
	
	public List<Critic> getCritics(int n, int criteria) {
		if (criteria == 1) {
			Collections.sort(this.critics,
					Collections.reverseOrder(Critic.comparator));
		} else {
			Collections.sort(this.critics, Critic.comparator);
		}
		int upperbound = this.critics.size();
		upperbound = (n > upperbound) ? upperbound : n;
		return this.critics.subList(0, upperbound);
	}
	
	public List<Critic> getCritics() {
		return this.critics;
	}
	
	public List<Job>  getRelatedJobs() {
		List<Job> jobs = new LinkedList<Job>();
		Job criticJob = null;
		for (Critic critic : this.critics) {
			criticJob = critic.getJob();
			if (!jobs.contains(criticJob)) {
				jobs.add(criticJob);
			}
		}
		return jobs;
	}

	/*
	public List<Location>  getRelatedLocations() {
		List<Location> locations = new LinkedList<Location>();
		Job criticLocation = null;
		for (Critic critic : this.critics) {
			criticLocation = critic.getLocation();
			if (!jobs.contains(criticJob)) {
				jobs.add(criticJob);
			}
		}
		return jobs;
	}
	*/
	
	public String getName() {
		return name;
	}
	
	public Industry getIndustry() {
		return this.industry;
	}

	protected void validate() {
		if (this.isVoid(this.name)) {
			this.addBrokenRule("Name", "El nombre de la compania es requerido.");
		}
		
		if(this.industry == null) {
			this.addBrokenRule("Industry", "La industria de la compania es requerida.");
		} else {
			this.addBrokenRule(this.industry.getBrokenRules());
		}
		
		if(this.critics != null) {
			for (Critic critic : this.critics) {
				this.addBrokenRule(critic.getBrokenRules());
			}
		}
	}
	
}

// eof
