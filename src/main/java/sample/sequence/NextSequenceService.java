package sample.sequence;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class NextSequenceService {
	@Autowired
	private MongoOperations mongo;

	public long getNextSequence(String seqName) {
		UserSequence counter = mongo.findAndModify(query(where("_seqName").is(seqName)), new Update().inc("seqId", 1),
				options().returnNew(true).upsert(true), UserSequence.class);
		return counter.getSeqId();
	}
}