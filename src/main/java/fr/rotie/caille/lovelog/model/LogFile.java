package fr.rotie.caille.lovelog.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "LogFile",
uniqueConstraints={
    @UniqueConstraint(name="logHash", columnNames={"logHash"})
})

public class LogFile extends LogEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private Integer logHash;
	
	private String fileName;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy="file")
    private Set<FileDay> fileDays = new HashSet<FileDay>();

	public Integer getLogHash() {
		return logHash;
	}

	public void setLogHash(Integer logHash) {
		this.logHash = logHash;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Set<FileDay> getFileDays() {
		return fileDays;
	}

	public void setFileDays(Set<FileDay> logDays) {
		this.fileDays = logDays;
	}

	@Override
	public String toString() {
		return "LogFile [id=" + id + ", fileName=" + fileName + "]";
	}

	public Long getId() {
		return id;
	}

}
