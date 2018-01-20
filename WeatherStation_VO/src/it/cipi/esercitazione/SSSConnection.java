package it.cipi.esercitazione;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Timer;

import com.google.gson.Gson;

import net.spreadsheetspace.sdk.Sdk;
import net.spreadsheetspace.sdk.StatusCode;
import net.spreadsheetspace.sdk.model.AddressBookDescriptor;
import net.spreadsheetspace.sdk.model.ChangeRecipientDescriptor;
import net.spreadsheetspace.sdk.model.Contact;
import net.spreadsheetspace.sdk.model.GenerateKeyDescriptor;
import net.spreadsheetspace.sdk.model.ListViewDescriptor;
import net.spreadsheetspace.sdk.model.ValuesViewDescriptor;
import net.spreadsheetspace.sdk.model.ViewDescriptor;

public class SSSConnection  extends Thread{
	static String server = "https://www.spreadsheetspace.net/";
	static String username = "S3984268@studenti.unige.it";
	static String password = "21foulo21";
	
	static String recipient1 = "g.camera@cipi.unige.it";
	

	static boolean createPublic = false;
	static boolean createPrivate = true;
	static HashMap <String, Object> inputs;
	
	
	public SSSConnection(String valoriDaParsare) {
		System.out.println(valoriDaParsare);
		Gson inputValuesJson= new Gson();
		System.out.println("FOULO");
		// Properties prop = (Properties) servletContext.getAttribute("properties");
		inputs = (HashMap <String, Object>)inputValuesJson.fromJson(valoriDaParsare, HashMap.class);
		System.out.println(inputs.toString());

	}
	
	
	public SSSConnection() {
		

	}
	
	/*
	 * Gli sleep sono stati inseriti solamente per dare il tempo di:
	 * - importare la vista creata su Excel
	 * - visualizzare i dati dallo script
	 */
	static int sleepTime = 30000;
	static boolean sleep = true;
	
	
	

	public void run() {
		try {
			Sdk sdk = new Sdk(server, username, password);
			System.out.println("init sdk");
			if(createPrivate) {
				privateViewExample(sdk);
			}
			
			if(createPublic) {
				publicViewExample(sdk);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static public void privateViewExample(Sdk sdk) {
		try {
			String view_id = "";
			String view_server = "";
			
			System.out.println("Generazione chiavi....");
			GenerateKeyDescriptor generateKeyDescriptor = sdk.generateKey();
			String privateKey = generateKeyDescriptor.getPrivateKey();
			System.out.println(privateKey);
			System.out.println("Chiave generata e salvata");
			
			int cols = 4;
			int rows = 30;
			String [][] table = new String[rows][cols];
			
			table[0][0] = "cc";//inputs.get("rain").toString();
			table[0][1] = "cc";//inputs.get("latitude").toString();
			table[0][2] ="cc"; //inputs.get("longitude").toString();
			table[0][3] ="cc"; //inputs.get("name").toString();

			
			LinkedList<String> listRecipients = new LinkedList<String>();
			listRecipients.add(recipient1);
			Set<String> recipients = new HashSet<String>(listRecipients);
			String excel_template = System.getProperty("user.dir") + "/template1.xlsx";
			
			System.out.println("\nCreazione vista privata....");
			ViewDescriptor viewDescriptor = sdk.createPrivateView("Test Range", recipients, table, excel_template, false, false, rows, cols);
			
			if(viewDescriptor.getStatusCode() == StatusCode.OK) {
				System.out.println("Vista privata creata");
				view_id = viewDescriptor.getViewId();
				view_server = viewDescriptor.getViewServer();
				
				System.out.println("\nVisualizzazione dati....");
				ValuesViewDescriptor valuesViewDescriptor = sdk.getValuesView(view_id, view_server, privateKey);
				Object[][] values = valuesViewDescriptor.getValues();
				
				int row = values.length;
				int col = values[0].length;
				for (int i=0; i<row; i++) {
					for (int j=0;j<col; j++) {
						System.out.println("(" + i + ", " + j + "): " + values[i][j]); 
					}
				}
				
				table = new String[rows][cols];
				table[0][0] = "1";
				table[0][1] = "2";
				table[1][0] = "3";
				table[1][1] = "4";
				
				excel_template = System.getProperty("user.dir") + "/template2.xlsx";
				
				if(sleep) {
					System.out.println("Sleep....");
					Thread.sleep(sleepTime);
				}
				
				System.out.println("\nAggiunta destinatari...." + recipient1);
				LinkedList<String> addListRecipients = new LinkedList<String>();
				addListRecipients.add(recipient1);
				
				ChangeRecipientDescriptor changeRecipientAddDescriptor = sdk.addRecipients(view_id, view_server, addListRecipients);
				if(changeRecipientAddDescriptor.getStatusCode() == StatusCode.OK) {
					System.out.println("Destinatari aggiunti.");
				} else {
					System.out.println("Errore nell'aggiunta dei destinatari: ");
					for(int i=0; i< changeRecipientAddDescriptor.getMessage().size(); i++) {
						System.out.println(changeRecipientAddDescriptor.getMessage().get(i));
					}
				}
				
				System.out.println("\nUpdate vista privata....");
				ViewDescriptor viewDescriptorUpdate;
				int attempts = 0;
				/*
				 * Do-While necessario perche' e' possibile che la vista a cui si sta facendo l'update sia stata aggiornata da Excel. In questo modo
				 * controllo se il numero di versione che ho memorizzato e' l'ultimo a disposizione o se e' necessario farmi restituire l'ultimo
				 * disponibile dal server.
				*/
				do {
					viewDescriptorUpdate = sdk.updateView(viewDescriptor, table, excel_template);
					
					if(viewDescriptorUpdate.getStatusCode() == StatusCode.WRONG_NEXT_NUMBER) {
						viewDescriptor.setNextAvailableSequenceNumber(viewDescriptorUpdate.getNextAvailableSequenceNumber());
					}
					attempts++;
				} while(viewDescriptorUpdate.getStatusCode() != StatusCode.OK && attempts < 5);
				
	
				if(viewDescriptorUpdate.getStatusCode() == StatusCode.OK) {
					System.out.println("Update della vista effettuato");
					
					ValuesViewDescriptor valuesViewDescriptorUpdate = sdk.getValuesView(view_id, view_server, privateKey);
					values = valuesViewDescriptorUpdate.getValues();
					
					row = values.length;
					col = values[0].length;
					for (int i=0; i<row; i++) {
						for (int j=0;j<col; j++) {
							System.out.println("(" + i + ", " + j + "): " + values[i][j]); 
						}
					}
					
					if(sleep) {
						System.out.println("Sleep....");
						Thread.sleep(sleepTime);
					}
					
					System.out.println("\nRimozione destinatari...." + recipient1);
					LinkedList<String> deleteListRecipients = new LinkedList<String>();
					deleteListRecipients.add(recipient1);
					
					ChangeRecipientDescriptor changeRecipientDeleteDescriptor = sdk.removeRecipients(view_id, view_server, deleteListRecipients);
					if(changeRecipientDeleteDescriptor.getStatusCode() == StatusCode.OK) {
						System.out.println("Destinatari rimossi.");
					} else {
						System.out.println("Errore nella rimozione dei destinatari.");
						for(int i=0; i< changeRecipientDeleteDescriptor.getMessage().size(); i++) {
							System.out.println(changeRecipientDeleteDescriptor.getMessage().get(i));
						}
					}
					
					if(sleep) {
						System.out.println("Sleep....");
						Thread.sleep(sleepTime);
					}
					
					System.out.println("\nEliminazione vista...");
					ViewDescriptor viewDescriptorDelete = sdk.deleteView(view_id, view_server);
					if(viewDescriptorDelete.getStatusCode() == StatusCode.OK) {
						System.out.println("Eliminazione effettuata");
					} else {
						System.out.println("Errore nell'eliminazione della vista");
						System.out.println(viewDescriptorDelete.getMessage());
						for(int i=0; i< viewDescriptorDelete.getMessages().size(); i++) {
							System.out.println(viewDescriptorDelete.getMessages().get(i));
						}
					}
				} else {
					System.out.println("Errore nell'update della vista");
					System.out.println(viewDescriptorUpdate.getMessage());
					for(int i=0; i< viewDescriptorUpdate.getMessages().size(); i++) {
						System.out.println(viewDescriptorUpdate.getMessages().get(i));
					}
				}
			} else {
				System.out.println("Errore nella creazione della vista");
				System.out.println(viewDescriptor.getMessage());
				for(int i=0; i< viewDescriptor.getMessages().size(); i++) {
					System.out.println(viewDescriptor.getMessages().get(i));
				}
			}
			
			System.out.println("\nRichiesta contatti...");
			AddressBookDescriptor addressBookDescriptor = sdk.getAddressBook();
			if(addressBookDescriptor.getStatusCode() == StatusCode.OK) {
				System.out.println("Contatti ricevuti.");
				Contact[] contacts = addressBookDescriptor.getListContact();
				
				for (int i = 0; i < contacts.length; i++) {
	                System.out.println(contacts[i].getFirstName() + ", " + contacts[i].getLastName() + " - " + contacts[i].getEmailAddress());
	            }
			} else {
				System.out.println("Errore nella richiesta dei contatti.");
			}
			
			System.out.println("\nRichiesta owned view...");
			ListViewDescriptor listOwnedDescriptor = sdk.getOwnedView();
			if(listOwnedDescriptor.getStatusCode() == StatusCode.OK) {
				System.out.println("Owned view ricevute.");
				for(int i=0; i<listOwnedDescriptor.getListView().size(); i++) {
					System.out.println(listOwnedDescriptor.getListView().get(i).getDescription() + " - " + listOwnedDescriptor.getListView().get(i).getOwner());;
				}
			} else {
				System.out.println("Errore nella richiesta delle owned view.");
			}
			
			System.out.println("\nRichiesta inbox view...");
			ListViewDescriptor listInboxDescriptor = sdk.getInboxView();
			if(listInboxDescriptor.getStatusCode() == StatusCode.OK) {
				System.out.println("Inbox view ricevute.");
				for(int i=0; i<listInboxDescriptor.getListView().size(); i++) {
					System.out.println(listInboxDescriptor.getListView().get(i).getDescription() + " - " + listInboxDescriptor.getListView().get(i).getOwner());;
				}
			} else {
				System.out.println("Errore nella richiesta delle inbox view.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static public void publicViewExample(Sdk sdk) {
		try {
			String view_id_public = "";
			String view_server_public = "";
			
			String excel_template = System.getProperty("user.dir") + "/template1.xlsx";
			
			int rows = 2;
			int cols = 2;
			
			String[][] table = new String[rows][cols];
			table[0][0] = "A";
			table[0][1] = "B";
			table[1][0] = "C";
			table[1][1] = "D";
			System.out.println("\nCreazione vista pubblica....");
			ViewDescriptor viewDescriptorPublic = sdk.createPublicView("Public Range", table, excel_template, false, false, rows, cols);
			
			if(viewDescriptorPublic.getStatusCode() == StatusCode.OK) {
				System.out.println("Vista pubblica creata");
				view_id_public = viewDescriptorPublic.getViewId();
				view_server_public = viewDescriptorPublic.getViewServer();
				
				System.out.println("\nVisualizzazione dati....");
				ValuesViewDescriptor valuesViewDescriptorPublic = sdk.getValuesView(view_id_public, view_server_public, "");
				Object[][] values = valuesViewDescriptorPublic.getValues();
				
				int row = values.length;
				int col = values[0].length;
				for (int i=0; i<row; i++) {
					for (int j=0;j<col; j++) {
						System.out.println("(" + i + ", " + j + "): " + values[i][j]); 
					}
				}
				
				System.out.println("\nPubblicazione vista pubblica....");
				ViewDescriptor viewDescriptorPublish = sdk.publishPublicView(view_id_public, view_server_public, "Public Range 2", "Descrizione vista pubblica");
				
				if(viewDescriptorPublish.getStatusCode() == StatusCode.OK) {
					System.out.println("Vista pubblica pubblicata correttamente.");
					
					if(sleep) {
						System.out.println("Sleep....");
						Thread.sleep(sleepTime);
					}
					
					table = new String[rows][cols];
					table[0][0] = "1";
					table[0][1] = "2";
					table[1][0] = "3";
					table[1][1] = "4";
					
					excel_template = System.getProperty("user.dir") + "/template2.xlsx";
					
					System.out.println("\nUpdate vista pubblica....");
					ViewDescriptor viewDescriptorUpdatePublic;
					int attempts = 0;
					/*
					 * Do-While necessario perche' e' possibile che la vista a cui si sta facendo l'update sia stata aggiornata da Excel. In questo modo
					 * controllo se il numero di versione che ho memorizzato e' l'ultimo a disposizione o se e' necessario farmi restituire l'ultimo
					 * disponibile dal server.
					*/
					do {
						viewDescriptorUpdatePublic = sdk.updateView(viewDescriptorPublic, table, excel_template);
						
						if(viewDescriptorUpdatePublic.getStatusCode() == StatusCode.WRONG_NEXT_NUMBER) {
							viewDescriptorPublic.setNextAvailableSequenceNumber(viewDescriptorUpdatePublic.getNextAvailableSequenceNumber());
						}
						attempts++;
					} while(viewDescriptorUpdatePublic.getStatusCode() != StatusCode.OK && attempts < 5);
					
					if(viewDescriptorUpdatePublic.getStatusCode() == StatusCode.OK) {
						System.out.println("Update della vista effettuato");
						
						ValuesViewDescriptor valuesViewDescriptorUpdate = sdk.getValuesView(view_id_public, view_server_public, "");
						values = valuesViewDescriptorUpdate.getValues();
						
						row = values.length;
						col = values[0].length;
						for (int i=0; i<row; i++) {
							for (int j=0;j<col; j++) {
								System.out.println("(" + i + ", " + j + "): " + values[i][j]); 
							}
						}
						
						if(sleep) {
							System.out.println("Sleep....");
							Thread.sleep(sleepTime);
						}
						
					} else {
						System.out.println("Errore durante l'Update della vista");
						System.out.println(viewDescriptorUpdatePublic.getMessage());
						for(int i=0; i< viewDescriptorUpdatePublic.getMessages().size(); i++) {
							System.out.println(viewDescriptorUpdatePublic.getMessages().get(i));
						}
					}
					
					System.out.println("\nEliminazione vista...");
					ViewDescriptor viewDescriptorDelete = sdk.deleteView(view_id_public, view_server_public);
					if(viewDescriptorDelete.getStatusCode() == StatusCode.OK) {
						System.out.println("Eliminazione effettuata");
					} else {
						System.out.println("Errore nell'eliminazione della vista");
						System.out.println(viewDescriptorDelete.getMessage());
						for(int i=0; i< viewDescriptorDelete.getMessages().size(); i++) {
							System.out.println(viewDescriptorDelete.getMessages().get(i));
						}
					}
				} else {
					System.out.println("Errore nella pubblicazione della vista");
					System.out.println(viewDescriptorPublic.getMessage());
					for(int i=0; i< viewDescriptorPublic.getMessages().size(); i++) {
						System.out.println(viewDescriptorPublic.getMessages().get(i));
					}
				}
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
