package application;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


public class Control implements Initializable {
	    @FXML
	    private Button enterIn;
	    @FXML
	    private Button newEnter;
	    @FXML
	    private TextField textFirst;
	    @FXML
	    private TextField textSecond;

	    @FXML
	    void getData(ActionEvent event){

	    	String firstTextField = textFirst.getText().toLowerCase();
	    	String secondTextField = textSecond.getText();

	    	//Check for correct data
	    	boolean firstFieldTest= checkDate(secondTextField);
	    	boolean secondFieldTest = checkMark(firstTextField);

	    	boolean infoEntry = checkBoth(firstFieldTest,secondFieldTest);
	    	connectToBase(firstTextField,secondTextField,infoEntry);

	    }

	static{
	    	try {
				Class.forName("com.ibm.db2.jcc.DB2Driver");
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }

	    @FXML
		void addNewData(ActionEvent event){
			textFirst.setText("");
			textSecond.setText("");
			enterIn.setText("Unesi");
		}
	    private void connectToBase(String firstTextField,String secondTextField,boolean infoEntry){

	    	if(!infoEntry){

	    		try {

	    			Connection con = null;
	    			String url = "jdbc:db2://localhost:50000/VSTUD";
	    			con = DriverManager.getConnection(url,"db2admin","abcdef");

	    				    		try {
	    				    			String sql =
	    				    					"with pom as( "
	    				    						+	"select * "
	    				    						+   "from ispit "
	    				    						+    "where datum_pismenog = ( "
	    				    						   + 	"select max(datum_pismenog) "
	    				    						    +	"from ispit"
	    				    						   +	")) "
	    				    			+ "select oznaka_roka,godina_roka "
	    				    			+ "from pom "
	    				    			+ "where datum_prijave = ( select max(datum_prijave) "
	    				    				+					"from pom) ";
	    				    			PreparedStatement preparedStmt = con.prepareStatement(sql);
	    				    			ResultSet result = preparedStmt.executeQuery();
	    				    			result.next();
	    				    			String stringMark = result.getString(1);
	    				    			String stringYear = result.getString(2);
	    				    			infoEntry = true;
	    				    			result.close();
	    								preparedStmt.close();
	    								con.close();
	    				    			connectToBase(stringMark, stringYear, infoEntry);
	    								//Closing result, statement and connection


	    							} catch (SQLException e) {
	    								// Multiuser environment
	    								if(e.getErrorCode() == -911 || e.getErrorCode() == -913){
	    									System.out.println("Objekat je zakljucan\n" + "Sacekati da se druga tranksacija zavrsi, probajte kasnije.");
	    								}
	    							}
	    							  catch(Exception e){
	    									  e.printStackTrace();
	    							  }
	    							}
	    							  catch(SQLException e){
    								  System.out.println("SQLCODE: " + e.getErrorCode() + "SQLSTATE: " + e.getSQLState() + "MESSAGE: "+ e.getMessage());
   							  }
	    							  catch(Exception e){
	    								  e.printStackTrace();
	    							  }

	    	}
		    else{

		    		try {
		    			/*
		    		 		a) Broj studenata izašlih na ispite
					 		b) Prosecnu ocenu
							c) Dobijene ocene i broj studenata sa tim ocenama (od 5 - 10)
							d) Predmete i procenat prolaznosti po ispitima, na pismenom, usmenom i
							ukupnu prolaznost (sortirane u opadajucem redosledu po ukupnoj prolaznosti)
		    			 */

		    			//TODO
		    			String sql = "with brStud as( "
						 	+ 	"select id_predmeta, "
							+	"sum(case when status_prijave ='o' then 1 else 0 end) brIzaslih, "
							+	"sum(case when ocena = 5 and status_prijave ='o' then 1 else 0 end) ocena5, "
							+	"sum(case when ocena = 6 and status_prijave ='o' then 1 else 0 end) ocena6, "
							+	"sum(case when ocena = 7 and status_prijave ='o' then 1 else 0 end) ocena7, "
							+	"sum(case when ocena = 8 and status_prijave ='o' then 1 else 0 end) ocena8, "
							+	"sum(case when ocena = 9 and status_prijave ='o' then 1 else 0 end) ocena9, "
							+	"sum(case when ocena = 10 and status_prijave ='o' then 1 else 0 end) ocena10, "
							+	"sum(case when datum_usmenog is not null or bodovi_usmenog is not null or(status_prijave = 'o' and ocena>5)then 1 else 0 end)brPismeni, "
							+	"sum(case when datum_usmenog is not null or bodovi_usmenog is not null then 1 else 0 end)brUsmeni, "
							+	"sum(case when ocena > 5 and status_prijave ='o' then 1 else 0 end) brPolozilo, "
							+	"avg(ocena*1.0) ocene "
							+	"from ispit "
							+	"where oznaka_roka=? and godina_roka=? "
							+	"and status_prijave ='o' "
							+	"group by id_predmeta) "
						 	+	"select naziv,brIzaslih,dec(ocene,5,2) prosek,ocena5,ocena6,ocena7,ocena8,ocena9,ocena10,dec((brPismeni*100)/(nullif(brIzaslih,0)),5,2)pismeni,dec((brUsmeni*100)/(nullif(brIzaslih,0)),5,2)usmeni,dec((brPolozilo*100)/(nullif(brIzaslih,0)),5,2)ukupno "
							+	"from brStud bs join predmet p "
							+	"on bs.id_predmeta = p.id_predmeta "
							+	"order by ukupno desc " ;



		    			Connection con = null;
		    			String url = "jdbc:db2://localhost:50000/VSTUD";
		    			con = DriverManager.getConnection(url,"db2admin","abcdef");
		    			PreparedStatement preparedStmt = con.prepareStatement(sql);

		    			int secondFieldInt = Integer.parseInt(secondTextField);
		    			preparedStmt.setString(1, firstTextField);
		    			preparedStmt.setInt(2, secondFieldInt);
		    			try {
		    				ResultSet result = preparedStmt.executeQuery();
		    				//Print result into file name.year.txt
		    				printIntoFile(firstTextField,secondTextField,result);
		    				//Closing result, statement and connection
		    				result.close();
		    				preparedStmt.close();
		    				con.close();

		    			} catch (SQLException e) {
		    				// Multiuser environment
		    				if(e.getErrorCode() == -911 || e.getErrorCode() == -913){
		    					System.out.println("Objekat je zakljucan\n" + "Sacekati da se druga tranksacija zavrsi, probajte kasnije.");
		    				}
		    			}
		    			catch(Exception e){
		    				e.printStackTrace();
		    				}
						}
		    			catch(SQLException e){
		    				System.out.println("SQLCODE: " + e.getErrorCode() + "SQLSTATE: " + e.getSQLState() + "MESSAGE: "+ e.getMessage());
		    			}
		    			catch(Exception e){
		    				e.printStackTrace();
		    			}
		    }
	    }

	    private void printIntoFile(String firstTextField, String secondTextField,ResultSet result) {

			PrintWriter printOut;
			try {
				printOut = new PrintWriter("rok"+firstTextField+"."+secondTextField+".txt");
			} catch (IOException e) {
				System.out.println("Failure to write file");
				e.printStackTrace();
				return;
			}
			//Print first default line
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			Date date = new Date();
			printOut.println("Matematički fakultet. " + dateFormat.format(date));

			//Print information about user entry
			printFirstFieldData(firstTextField, printOut, secondTextField);

			//Print all information about entry
			try {
				while(result.next()){
					printOut.println();
					printOut.println("-------------------------------------------------------------------------------------");
					printOut.println();
					printOut.println("Naziv predmeta: " + result.getString(1));
					printOut.println("Broj izaslih: " + result.getInt(2));
					printOut.println("Prosecna ocena: " + result.getDouble(3));
					printOut.println("Broj studenata sa ocenom 5: "+ result.getInt(4));
					printOut.println("Broj studenata sa ocenom 6: " + result.getInt(5));
					printOut.println("Broj studenata sa ocenom 7: " + result.getInt(6));
					printOut.println("Broj studenata sa ocenom 8: " + result.getInt(7));
					printOut.println("Broj studenata sa ocenom 9: " + result.getInt(8));
					printOut.println("Broj studenata sa ocenom 10: " +result.getInt(9));
					printOut.println("Uspesnost pismenog: " + result.getDouble(10));
					printOut.println("Uspesnost usmenog: " + result.getDouble(11));
					printOut.println("Uspesnost ukupno: " + result.getDouble(12));
					printOut.flush();
				}
				enterIn.setText("Izvrseno!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			printOut.close();
		}
	    private void printFirstFieldData(String firstTextField,PrintWriter printOut ,String secondTextField){
	    	int secondFieldInt = Integer.parseInt(secondTextField);
	    	switch(firstTextField){
	    	case "apr":
	    		printOut.println("Ispitni rok: April " + secondFieldInt);
	    		break;
	    	case "dod":
	    		printOut.println("Ispitni rok: Dodatni " + secondFieldInt);
	    		break;
	    	case "feb":
	    		printOut.println("Ispitni rok: Februar " + secondFieldInt);
	    		break;
	    	case "jan":
	    		printOut.println("Ispitni rok: Januar " + secondFieldInt);
	    		break;
	    	case "jan1":
	    		printOut.println("Ispitni rok: Januar1 " + secondFieldInt);
	    		break;
	    	case "jun":
	    		printOut.println("Ispitni rok: Jun " + secondFieldInt);
	    		break;
	    	case "jun1":
	    		printOut.println("Ispitni rok: Jun1 " + secondFieldInt);
	    		break;
	    	case "jun2":
	    		printOut.println("Ispitni rok: Jun2 " + secondFieldInt);
	    		break;
	    	case "okt":
	    		printOut.println("Ispitni rok: Oktobar " + secondFieldInt);
	    		break;
	    	case "sep":
	    		printOut.println("Ispitni rok: Septmebar " + secondFieldInt);
	    		break;
	    	case "sep1":
	    		printOut.println("Ispitni rok: Septembar1 " + secondFieldInt);
	    		break;
	    	case "sep12":
	    		printOut.println("Ispitni rok: Septembar12 " + secondFieldInt);
	    		break;
	    	case "sep2":
	    		printOut.println("Ispitni rok: Septembar2 " + secondFieldInt);
	    		break;
	    	default:
	    			break;
	    	}
	    }
	    private boolean checkDate(String secondTextField) {
			//Check date
	    	boolean goodDate = secondTextField.matches("^(19|20)\\d{2}$");
	    	if(!goodDate){
	    		return false;
	    	}
	    	else{
	    		return true;
	    	}
	    }

		private boolean checkMark(String firstTextField) {
			// Check correct name
			switch(firstTextField){
			case "dod":
			case "jan":
			case "jan1":
			case "feb":
			case "apr":
			case "jun":
			case "jun1":
			case "jul":
			case "sep":
			case "sep1":
			case "sep12":
			case "sep2":
			case "okt":
				return true;
			default:
				return false;
			}
		}

		private boolean checkBoth(boolean firstFieldTest,boolean secondFieldTest) {
			// Check if both is correct
	    	if(firstFieldTest && secondFieldTest){
	    		return true;
	    	}
			return false;
		}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}

}
