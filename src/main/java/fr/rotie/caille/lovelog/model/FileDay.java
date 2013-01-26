package fr.rotie.caille.lovelog.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "FileDay",
uniqueConstraints={
    @UniqueConstraint(name="fileDay", columnNames={"idFile", "idDay"})
})
public class FileDay {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name="idFile", nullable=false)
    private LogFile file;
	
	@Column(nullable=false)
	private Integer idDay;

	public LogFile getFile() {
		return file;
	}

	public void setFile(LogFile file) {
		this.file = file;
	}

	public Integer getIdDay() {
		return idDay;
	}

	public void setIdDay(Integer iDday) {
		this.idDay = iDday;
	}

	public Long getId() {
		return id;
	}
}
