package sample.sequence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customSequences")
public class CustomSequences {
	@Id
	private String seqName;
	private long seqId;

	public String getSeqName() {
		return seqName;
	}

	public void setSeqName(String seqName) {
		this.seqName = seqName;
	}

	public long getSeqId() {
		return seqId;
	}

	public void setSeqId(long seqId) {
		this.seqId = seqId;
	}

}
