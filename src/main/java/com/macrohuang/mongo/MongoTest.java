package com.macrohuang.mongo;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoOptions;

public class MongoTest {
	public static class StressTest implements Runnable {
		Mongo mongo;
		DB db;
		int num;
		long accountId;

		public StressTest(int num, Mongo mongo, long accountId) {
			this.num = num;
			this.mongo = mongo;
			this.accountId = accountId;
		}

		public StressTest(int num, DB db, long accountId) {
			this.num = num;
			this.db = db;
			this.accountId = accountId;
		}
		@Override
		public void run() {
			Long l = System.currentTimeMillis();
			// DB db = mongo.getDB("CPCREPORT_2012");
			DBCollection collection = db.getCollection("CPCIDEA");
			// DBCursor cursor = collection.find(new BasicDBObject("ACCOUNTID",
			// accountId));
			
			DBCursor cursor = collection.find(new BasicDBObject(new HashMap() {
				/**
				 * 
				 */
				private static final long serialVersionUID = -5849089715249711585L;

				{
					put("ACCOUNTID", new HashMap() {
						/**
						 * 
						 */
						private static final long serialVersionUID = -8005541086557745860L;

						{
							put("$gte", 200000);
							put("$lt", 300000);
						}
					});
				}
			}));
			System.out.println(String.format("No. %d query with accountID: %d  cost: %d, total count:%d", num, accountId, System.currentTimeMillis()
					- l,
					cursor.count()));
			l = System.currentTimeMillis();
			cursor.batchSize(1000);
			int count = 0;
			for (DBObject object : cursor) {
				count++;
				object.containsField("ACCOUNTID");
			}
				;
			// while (cursor.hasNext()) {
			// cursor.next();
			// }
			System.out.println(String.format("No. %d, iterate %d objects with accountID: %d cost: %d", num, count, accountId,
					System.currentTimeMillis() - l));
			cursor.close();
		}
	}

	/**
	 * @param args
	 * @throws MongoException
	 * @throws UnknownHostException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws UnknownHostException, MongoException, InterruptedException {
		MongoOptions options = new MongoOptions();
		options.autoConnectRetry = true;
		options.connectionsPerHost = 50;
		options.connectTimeout = 20 * 1000;
		options.socketTimeout = 600 * 1000;
		options.threadsAllowedToBlockForConnectionMultiplier = 100;
		Mongo mongo = new Mongo("10.11.203.56:8888", options);

		Long l = System.currentTimeMillis();
		int concurrent = 1;

		// CyclicBarrier barrier = new CyclicBarrier(concurrent);
		CountDownLatch latch = new CountDownLatch(concurrent);
		int[] accountIds = new int[] { 347272, 347278, 347302, 347303, 347304, 347307, 347308, 347314, 347322, 347323, 347324, 347326, 347330,
				347334, 347335, 347337, 347353, 347354, 347356, 347365, 347367, 347368, 347372, 347373, 347375, 347378, 347379, 347382, 347384,
				347385, 347400, 347405, 347424, 347431, 347433, 347436, 347442, 347443, 347449, 347450, 347455, 347456, 347460, 347469, 347478,
				347512, 347513, 347514, 347526, 347529, 347539, 347552, 347567, 347570, 347572, 347575, 347591, 347595, 347599, 347603, 347605,
				347606, 347610, 347617, 347618, 347621, 347622, 347627, 347630, 347631, 347647, 347654, 347670, 347673, 347685, 347688, 347690,
				347698, 347715, 347717, 347721, 347722, 347724, 347725, 347726, 347739, 347740, 347753, 347759, 347770, 347783, 347799, 347813,
				347820, 347821, 347834, 347839, 347841, 347846, 347851, 347862, 347864, 347871, 347874, 347877, 347878, 347881, 347912, 347921,
				347922, 347924, 347926, 347930, 347934, 347936, 347939, 347948, 347949, 347952, 347958, 347960, 347963, 347972, 347974, 347984,
				347991, 347993, 347997, 348001, 348005, 348008, 348009, 348016, 348017, 348021, 348043, 348046, 348074, 348076, 348081, 348084,
				348085, 348086, 348092, 348093, 348112, 348116, 348117, 348134, 348158, 348162, 348165, 348166, 348174, 348175, 348180, 348185,
				348188, 348192, 348194, 348195, 348197, 348198, 348201, 348206, 348212, 348218, 348222, 348223, 348236, 348242, 348244, 348248,
				348249, 348250,
				348255, 348257, 348267, 348271, 348281, 348285, 348289, 348294, 348300, 348303, 348304, 348307, 348311, 348335, 348342, 348343,
				348344, 348345, 348351, 348352, 348356, 348358, 348360, 348363, 348365, 348367, 348371, 348382, 348386, 348388, 348389, 348403,
				348409, 348411, 348417, 348419, 348424, 348427, 348428, 348429, 348430, 348445, 348451, 348456, 348464, 348465, 348467, 348468,
				348469, 348492, 348513, 348525, 348528, 348529, 348534, 348535, 348537, 348541, 348542, 348546, 348550, 348575, 348578, 348581,
				348586, 348587, 348614, 348615, 348626, 348630, 348631, 348633, 348634, 348635, 348638, 348641, 348642, 348647, 348659, 348662,
				348668, 348672, 348678, 348680, 348683, 348688, 348690, 348692, 348705, 348708, 348711, 348723, 348725, 348734, 348743, 348745,
				348749, 348761, 348763, 348764, 348766, 348773, 348779, 348787, 348804, 348808, 348810, 348812, 348814, 348829, 348833, 348836,
				348838, 348839, 348842, 348853, 348859, 348860, 348861, 348865, 348866, 348867, 348869, 348870, 348871, 348873, 348876, 348887,
				348889, 348890, 348901, 348906, 348909, 348912, 348913, 348914, 348922, 348924, 348925, 348926, 348934, 348940, 348944, 348946,
				348947, 348952, 348956, 348970, 348992, 349017, 349018, 349022, 349024, 349025, 349027, 349033, 349034, 349035, 349036, 349037,
				349038, 349040, 349042, 349043, 349046, 349047, 349062, 349064, 349068, 349070, 349073, 349088, 349101, 349103, 349114, 349125,
				349126, 349128, 349130, 349132, 349133, 349137, 349140, 349144, 349147, 349148, 349150, 349151, 349154, 349160, 349165, 349169,
				349171, 349172, 349174, 349182, 349183, 349189, 349190, 349193, 349194, 349195, 349196, 349198, 349199, 349201, 349202, 349206,
				349210, 349212, 349213, 349215, 349216, 349217, 349233, 349235, 349242, 349249, 349253, 349257, 349259, 349268, 349270, 349271,
				349274, 349277, 349290, 349306, 349310, 349311, 349312, 349314, 349335, 349341, 349356, 349363, 349364, 349365, 349367, 349371,
				349377, 349378, 349385, 349388, 349389, 349393, 349396, 349397, 349399, 349402, 349408, 349410, 349415, 349427, 349437, 349438,
				349439, 349444, 349451, 349459, 349461, 349467, 349472, 349473, 349474, 349477, 349478, 349488, 349490, 349493, 349504, 349505,
				349507, 349509, 349520, 349523, 349531, 349532, 349540, 349544, 349545, 349551, 349555, 349558, 349583, 349585, 349598, 349599,
				349600, 349607, 349611, 349612, 349618, 349625, 349629, 349640, 349645, 349655, 349657, 349658, 349659, 349663, 349664, 349667,
				349670, 349672, 349675, 349679, 349686, 349687, 349688, 349691, 349697, 349704, 349709, 349710, 349715, 349724, 349731, 349733,
				349736, 349741, 349742, 349745, 349750, 349756, 349759, 349764, 349767, 349775, 349776, 349789, 349791, 349797, 349798, 349799,
				349801, 349802, 349804, 349805, 349809, 349810, 349816, 349820, 349823, 349824, 349827, 349829, 349830, 349839, 349840, 349841,
				349843, 349849, 349850, 349853, 349854, 349868, 349869, 349870, 349880, 349881, 349892, 349906, 349919, 349933, 349936, 349939,
				349954, 349959, 349967, 349969, 349971, 349973, 349977, 349984, 349988, 349991, 349992, 349997 };
		Random random = new Random();
		for (int i = 0; i < concurrent; i++) {
			// new Thread(new StressTest(i, mongo,
			// Long.valueOf(accountIds[random.nextInt(accountIds.length)]))).start();
			new Thread(new StressTest(i, mongo.getDB("CPCREPORT_2012"), Long.valueOf(accountIds[random.nextInt(accountIds.length)]))).start();
			latch.countDown();
		}
		latch.await();
		System.out.println("Time cost:" + (System.currentTimeMillis() - l));
	}
}
