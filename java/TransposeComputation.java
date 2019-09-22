import java.nio.file.*;
import java.nio.charset.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
import java.lang.*;

//Note: Lines 25, 126, and 127 can be modified
class TransposeComputation {


//I apologize to anyone who actually knows how to DOM.
public static void main(String[] args) {
	
	long start = System.currentTimeMillis();
	boolean debug = true;
	
	//temp contains name of uploaded pdf
	String temp = args[0];
	String from_inst_key = temp.substring(0, temp.charAt(1)=='$' ? 1: 2);
	String to_inst_key = temp.substring(temp.charAt(1)=='$' ? 2 : 3, temp.charAt(1)=='$' ? (temp.charAt(3)=='$' ? 3 : 4) : (temp.charAt(4)=='$' ? 4 : 5));

	if(debug) {
	    System.out.println(from_inst_key);
	    System.out.println(to_inst_key);
	}
	int fifths_change = get_fifths_change(from_inst_key, to_inst_key);
	
	Path filepath = Paths.get("/var/www/html/mxl/original/" + temp.substring(0, temp.length() - 3) + "xml");
	
	try {
	
	Scanner reader = new Scanner(filepath);
	Scanner anotherReader;
	
	//Finds audiveris-detected key
	int key = 0;
	
	//get string containing key
	reader.useDelimiter("\\<measure");
	reader.next();
	reader.useDelimiter("attributes\\>");
	reader.next();
	temp = reader.next(); //temp represents string containing key info
	
	int indice;
	if ((indice=temp.indexOf("fifths"))!=-1) {
			key = Integer.parseInt(temp.substring(indice+7,(temp.charAt(indice+7)=='-') ? indice+9 : indice+8));
	}
	
	if (debug) {
		//System.out.println(temp);
		
		// System.out.println("fifths_change: " + fifths_change);
		// System.out.println("key: " + key);
	}
	
	reader.useDelimiter("pitch\\>");
	ArrayList<String> pitchList = new ArrayList<String>();
	//main loop, creates transposed MusicXML and saves to tempArray
	while(reader.hasNext()) {
		String step = "C";
		int alter = 0;
		int octave = 4;
		temp = "";
		
		reader.next(); //skips unimportant info between </pitch> and <pitch>
		if(reader.hasNext())
			temp = reader.next(); //temp represent string containing pitch info
		else
			break;
		// if(debug)
			// System.out.println(temp);
		anotherReader = new Scanner(temp);
		anotherReader.useDelimiter("\\n");
		
		while(anotherReader.hasNext()) {
			temp = anotherReader.next(); //temp represents line in pitch element
			if (debug)
				System.out.println(temp);
			if(temp.length()<12)
				if(anotherReader.hasNext())
					continue;
				else
					break;
			if(temp.substring(11,12).equals("s"))
				step = temp.substring(16,17);
			if(temp.substring(11,12).equals("a"))
			    alter = Integer.parseInt(temp.substring(17,(temp.charAt(17)=='-') ? 19 :18));
			if(temp.substring(11,12).equals("o"))
				octave = Integer.parseInt(temp.substring(18,19));
		}
		if (debug) {
			System.out.println("step: "   + step   + "\n"
							 + "alter: "  + alter  + "\n"
							 + "octave: " + octave);
		}
		anotherReader.close();
		
		//End DOM shit, start actual music calculations
		
		int new_step = 1;
		int new_alter = 0;
		int new_octave = 4;
		
		new_octave = octave + get_new_octave(step, alter, fifths_change);
		
		new_step = get_new_step(step, fifths_change);
		
		new_alter = get_new_alter(string_to_step(step), new_step, alter, key, fifths_change);
		
		String currPitch = "<pitch>"                                         + "\n"
		             + "  <step>" + step_to_string(new_step) + "</step>" + "\n"
					 + "  <alter>" + new_alter + "</alter>"              + "\n"
					 + "  <octave>" + new_octave + "</octave>"           + "\n"
					 + "</pitch>";
					 
		if (debug) {
			//System.out.println(get_new_octave(step, alter, get_semitone_change(fifths_change)));
			// System.out.println("------");
			// System.out.println("new_step: "   + new_step   + "\n"
							 // + "new_alter: "  + new_alter  + "\n"
							 // + "new_octave: " + new_octave);
			// System.out.println("------");
			System.out.println(currPitch);
		}
		pitchList.add(currPitch);
		//System.out.println(pitchList.size());
	}
	reader.close();
	//End music caluclations, start editing file
	
	File musicXML = new File("/var/www/html/mxl/original/" + args[0].substring(0, args[0].length() - 3) + "xml");
	File tempF = new File("/var/www/html/mxl/transposed/" + args[0].substring(0, args[0].length() - 3) + "xml");
	tempF.createNewFile();
	
	String oldContent = "";
	
	BufferedReader buffR = new BufferedReader(new FileReader(musicXML));
	PrintStream writer = new PrintStream(tempF);
	
	String line = "";
	boolean keyFlag = false;
	for (int i=0; (line = buffR.readLine())!=null;) {
            if(line.indexOf("<pitch>")!=-1)
				line = printPitch(pitchList, i++, buffR, writer);
			else if(line.indexOf("<attributes>")!=-1 && keyFlag==false) {
				keyFlag = true;
				addKey(key, writer, fifths_change);
			}
			else if(line.indexOf("<key>")!=-1) {
				buffR.readLine();
				buffR.readLine();
			}
				
            else
				writer.println(line);
    }
	
	// Attempt 2
	// while((line=buffR.readLine()) != null) {
		// oldContent += oldContent + line + System.lineSeparator();
	// }
	// System.out.println(oldContent.length());
	
	// String newContent = "";
	// int indice1 = 0;
	// int indice2 = 0;
	// for(String pitch : pitchList) {
		
		// indice2 = oldContent.indexOf("<pitch>", indice1);
		// newContent += oldContent.substring(indice1, indice2);
		// System.out.println(oldContent.substring(indice1, indice2));
		
		// newContent += pitch;
		
		// indice1 = oldContent.indexOf("</pitch>", indice2);
	// }
	
	//Attempt 1
	// while((indice2=oldContent.indexOf("<pitch>", indice))!= -1) {
		// newContent += oldContent.substring(indice1, indice2);
		// System.out.println(oldContent.substring(indice1, indice2);
		
		
	// }
	
	buffR.close();
	writer.close();
	
	}
	catch(Exception e) {
		System.out.println("Waa.");
		e.printStackTrace();
	}
	finally {
		if(debug) {
			System.out.println(" " + System.lineSeparator() + ".");
			long end = System.currentTimeMillis();
			System.out.println("You suck.");
			Long result = end - start;
			System.out.println(result.toString());
		}
	}
	
}

public static int get_new_octave(String step, int alter, int fifths_change) {
	int note = 0;
	switch (step.charAt(0)) {
	case 'C': note=0; break;
	case 'D': note=1; break;
	case 'E': note=2; break;
	case 'F': note=3; break;
	case 'G': note=4; break;
	case 'A': note=5; break;
	case 'B': note=6; break;
	}
	// System.out.println(note);
	note += (4*fifths_change) % 7;
	// System.out.println(note);
	
	return note>=7 ? 1 : (note>= 0 ? 0 : -1);
}

public static int get_new_step(String step, int fifths_change) {
	int note = 0;
	note = string_to_step(step);
	
	note = (note+4*fifths_change) % 7;
	if (note<=0)
		note +=7;
	
	return note;
}

public static int get_new_alter(int step, int new_step, int alter, int key, int fifths_change) {
	int[][] pieceOfShit = {{-1, -1, -1, -1, -1, -1, -1}//Cb -7
  						 , {-1, -1, -1, 0, -1, -1, -1} //Gb -6
						 , {0, -1, -1, 0, -1, -1, -1}  //Db -5
						 , {0, -1, -1, 0, 0, -1, -1}   //Ab -4
						 , {0, 0, -1, 0, 0, -1, -1}     //Eb -3
						 , {0, 0, -1, 0, 0, 0, -1}     //Bb -2
						 , {0, 0, 0, 0, 0, 0, -1}      //F  -1
						 , {0, 0, 0, 0, 0, 0, 0}       //C   0
						 , {0, 0, 0, 1, 0, 0, 0}       //G   1
						 , {1, 0, 0, 1, 0, 0, 0}       //D   2
						 , {1, 0, 0, 1, 1, 0, 0}       //A   3
						 , {1, 1, 0, 1, 1, 0, 0}       //E   4
						 , {1, 1, 0, 1, 1, 1, 0}       //B   5
						 , {1, 1, 1, 1, 1, 1, 0}       //F#  6
	                     , {1, 1, 1, 1, 1, 1, 1}};     //C#  7
	
	int degree_alter = alter - pieceOfShit[key+7][step-1];
	// System.out.println(alter);
	// System.out.println(key);
	// System.out.println(step);
	// System.out.println(degree_alter);

	int super_temp = key + fifths_change;
	while (super_temp>7)
	    super_temp-=12;
	while(super_temp<-7)
	    super_temp+=12;
	int new_alter = degree_alter + pieceOfShit[super_temp+7][new_step-1];
	
	return new_alter;
}

public static String printPitch(ArrayList<String> pitchList, int i, BufferedReader buffR, PrintStream writer) throws Exception {
	
	String line = "";
	
	Scanner reader = new Scanner(pitchList.get(i));
	reader.useDelimiter("\\n");
	while(reader.hasNext())
		writer.println("        " + reader.next());
	
	while((line=buffR.readLine()).indexOf("</pitch>")==-1);
	return line;
}

public static void addKey(int key, PrintStream writer, int fifths_change) {

    int temp = key+fifths_change;
    while(temp>7)
	temp -=12;
    while(temp<-7)
	temp +=12;
        writer.println("      <attributes>");
	writer.println("        <key>");
	writer.println("          <fifths>" + (key+fifths_change) + "</fifths>");
	writer.println("        </key>");
}

public static String step_to_string(int step) {
	switch (step) {
		case 1:  return "C";
		case 2:  return "D";
		case 3:  return "E";
		case 4:  return "F";
		case 5:  return "G";
		case 6:  return "A";
		case 7:  return "B";
		default: return "C";
	}
}

public static int string_to_step(String step) {
	switch (step.charAt(0)) {
		case 'C':  return 1;
		case 'D':  return 2;
		case 'E':  return 3;
		case 'F':  return 4;
		case 'G':  return 5;
		case 'A':  return 6;
		case 'B':  return 7;
		default:   return 1;
	}
}

public static int get_semitone_change(int fifths_change) {
	return (7*fifths_change) % 12;
}

public static int get_fifths_change(String from_inst_key, String to_inst_key) {
    int temp = get_key_in_fifths(from_inst_key) - get_key_in_fifths(to_inst_key);
    return temp;
}

public static int get_key_in_fifths(String key) {
	int temp = 0;
	switch(key.charAt(0)) {
		case 'F':
		temp = -1;
		break;
		case 'C':
		temp = 0;
		break;
		case 'G':
		temp = 1;
		break;
		case 'D':
		temp = 2;
		break;
		case 'A':
		temp = 3;
		break;
		case 'E':
		temp = 4;
		break;
		case 'B':
		temp = 5;
	}
	if(key.length()>1) {
		if(key.substring(1,2).equals("#"))
			temp += 7;
		else if(key.substring(1,2).equals("b"))
			temp -=7;
	}
	System.out.println("Get key function says: " + temp);
	return temp;
}
}
