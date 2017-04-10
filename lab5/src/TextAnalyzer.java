import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

//my imports
import java.io.*;
import org.apache.hadoop.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;
// Do not change the signature of this class
public class TextAnalyzer extends Configured implements Tool {

    // Replace "?" with your own output key / value types
    // The four template data types are:
    //     <Input Key Type, Input Value Type, Output Key Type, Output Value Type>
    public static class TextMapper extends Mapper<LongWritable, Text, Text, WordCount> {
				private WordCount wc = new WordCount();;
				private Text word = new Text();
        public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException
        {
            // Implementation of you mapper function
						String val = value.toString().toLowerCase();
						char[] newVal = val.toCharArray();
						for (int i = 0; i < newVal.length; i++){
							char c = newVal[i];
							if ((c < 'a' || c > 'z') && (c < '0' || c > '9')) {
								newVal[i] = ' ';
							}
						}
						val = new String(newVal);
						/*StringTokenizer st = new StringTokenizer(val);
						while (st.hasMoreTokens()) {
							line.add(st.nextToken());
						}*/
						ArrayList<String> line2 = new ArrayList<String>();
						String[] line = val.split("\\s");
						for (int i = 0; i < line.length; i++) {
							if (!(line[i].equals(new String("")))) {line2.add(line[i]);}
						}
						line = new String[line2.size()];
						line = line2.toArray(line);
						for (int i = 0; i < line.length; i++) {
							for (int j = 0; j < line.length; j++) {
								if (j != i) {
									wc.set(line[j], 1);
									word.set(line[i]);
									context.write(word, wc);
								}
							}
						}
        }
    }

    // Replace "?" with your own key / value types
    // NOTE: combiner's output key / value types have to be the same as those of mapper
    public static class TextCombiner extends Reducer<Text, WordCount, Text, WordCount> {
        public void reduce(Text key, Iterable<WordCount> wc, Context context)
            throws IOException, InterruptedException
        {
            // Implementation of you combiner function
						//arraylist implementation
						ArrayList<WordCount> combo = new ArrayList<WordCount>();
						WordCount query;
						System.out.println("Key: " + key.toString());
						for (WordCount next:wc) {
							System.out.println("This WordCount: " + next.toString());
							query = new WordCount(next.getWord(), next.getCount());
							int firstOccurence = combo.indexOf(query);
							if (firstOccurence == -1) {
								System.out.println("Added: " + query.toString());
								combo.add(new WordCount(query));
							}
							else {
								System.out.println("First value in combo: " + combo.get(0).toString());
								WordCount add = combo.get(firstOccurence);
								add.set(add.getCount().get() + query.getCount().get());
								System.out.println(add.toString() + " query: " + query.toString());
								combo.set(firstOccurence, add);
							}
						}
						for (WordCount next : combo) {
							System.out.println("Length of combo: "+ combo.size());
							context.write(key, next);
						}
						System.out.println("");
        }
    }

    // Replace "?" with your own input key / value types, i.e., the output
    // key / value types of your mapper function
    public static class TextReducer extends Reducer<Text, WordCount, Text, Text> {
        private final static Text emptyText = new Text("");

        public void reduce(Text key, Iterable<WordCount> wcs, Context context)
            throws IOException, InterruptedException
        {
            // Implementation of you reducer function

            // Write out the results; you may change the following example
            // code to fit with your reducer function.
            //   Write out the current context key
            context.write(key, emptyText);

						//write out query word and count
						Text count = new Text();
						Text qWord = new Text();
						for (WordCount query:wcs) {
							//WordCount query = wc.next();
							count.set(query.getCount().toString()+">");
							qWord.set("<"+query.getWord().toString()+",");
							context.write(qWord, count);
						}
            //   Write out query words and their count
            /*for(String queryWord: map.keySet()){
                String count = map.get(queryWord).toString() + ">";
                queryWordText.set("<" + queryWord + ",");
                context.write(queryWordText, new Text(count));
            }*/
            //   Empty line for ending the current context key
            context.write(emptyText, emptyText);
        }
    }

    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();

        // Create job
        Job job = new Job(conf, "EID1_EID2"); // Replace with your EIDs
        job.setJarByClass(TextAnalyzer.class);

        // Setup MapReduce job
        job.setMapperClass(TextMapper.class);
        //   Uncomment the following line if you want to use Combiner class
        job.setCombinerClass(TextCombiner.class);
        job.setReducerClass(TextReducer.class);

        // Specify key / value types (Don't change them for the purpose of this assignment)
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        //   If your mapper and combiner's  output types are different from Text.class,
        //   then uncomment the following lines to specify the data types.
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(WordCount.class);

        // Input
        FileInputFormat.addInputPath(job, new Path(args[0]));
        job.setInputFormatClass(TextInputFormat.class);

        // Output
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.setOutputFormatClass(TextOutputFormat.class);

        // Execute job and return status
        return job.waitForCompletion(true) ? 0 : 1;
    }

    // Do not modify the main method
    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new TextAnalyzer(), args);
        System.exit(res);
    }

    // You may define sub-classes here. Example:
    // public static class MyClass {
    //
    // }
		public static class WordCount implements WritableComparable {
			//components
			private Text word;
			private IntWritable count;
			
			//constructor
			public WordCount(Text w, IntWritable c) {
				this.word = w;
				this.count = c;
			}

			public WordCount(String w, int c) {
				this.word = new Text(w);
				this.count = new IntWritable(c);
			}
			
			public WordCount() {
				this.word = new Text();
				this.count = new IntWritable(1);
			}
			//copy constructor
			public WordCount(WordCount other) {
				this.word = new Text(other.getWord().toString());
				this.count = new IntWritable(other.getCount().get());
			}

			//access components
			public Text getWord() {
				return this.word;
			}

			public IntWritable getCount() {
				return this.count;
			}
			
			public void set(String w) {
				this.word.set(w);
			}

			public void set(int i) {
				this.count.set(i);
			}

			public void set(String w, int c) {
				this.word.set(w);
				this.count.set(c);
			}
			
			//interface
			@Override
			public void readFields(DataInput in) throws IOException {
				this.word.readFields(in);
				this.count.readFields(in);
			}

			@Override
			public void write(DataOutput out) throws IOException {
				this.word.write(out);
				this.count.write(out);
			}

			@Override
			public int compareTo(Object tp) {
				if (tp instanceof WordCount) {
					WordCount wc = (WordCount)tp;
					return word.compareTo(wc.getWord());
				} else {
					return 1;
				}
			}
			
			//object
			@Override
			public String toString() {
				return word.toString() + " " + count.toString();
			}

			@Override
			public int hashCode() {
				return word.hashCode()*157 + count.hashCode();
			}

			@Override
			public boolean equals(Object o) {
				if (o instanceof WordCount) {
					WordCount other = (WordCount)o;
					return this.word.toString().equals(other.word.toString());
				}
				else {return false;}
			}

		}
}
