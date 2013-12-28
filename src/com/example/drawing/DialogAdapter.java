package com.example.drawing;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class DialogAdapter {

	private Context kontekst;
	private SeekBar sb;
	private TextView tv;

	// Po defaultu, stavio sam da je boja crvena (0), a debljina 50
	private int debljina = 50;
	//private int boja = 0;

	RadioGroup rg;

	// U konstruktoru se traĹži kontekst, jer Äe nam trebat na par mjesta
	DialogAdapter(Context k) {
		kontekst = k;
	}


	// Ove dvije metode sam stavio da se mogu saznat trenutno odabrane debljina i boja
	public int dobijDebljinu() {
		return debljina;
	}


	//////// POSTAVLJANJE 1. DIALOGA (ZA DEBLJINU) ////////
	public void postaviDialogZaDebljinu() {
		// Treba biti tipa final. Ne znam zaĹĄto, al inaÄe ne radi. Ugl., potrebno mu je proslijediti kontekst View-a u kojem se trenutno nalazimo, a to upravo i Äinimo.
		final Dialog dialog = new Dialog(kontekst);

		// Stavljamo sadrĹžaj u dialog. U ovom sluÄaju, to je dialogdebljina.xml koji se nalazi u /res/layout/ folderu
		dialog.setContentView(R.layout.dialogdebljina);

		// KozmetiÄki, al opet korisno
		dialog.setTitle("Choose size:");

		// Treba dodati gumb za potvrdu debljine, odnosno dismiss dialoga
		Button ok = (Button) dialog.findViewById(R.id.bDebljinaOK);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CrtanjeView.boja.setStrokeWidth((float) debljina);
				CrtanjeView.putanja = new mojaPutanja(new Paint(CrtanjeView.boja));
		        CrtanjeView.paths.add(CrtanjeView.putanja);
				dialog.dismiss();
			}
		});

		// Bez ovoga, dialog bi bio postavljen, al ne i prikazan. To sam nauÄio na teĹži naÄin :D
		dialog.show();

		// TextView u kojem piĹĄe trenutna vrijednost klizaÄa. Isto kozmetiÄki, al je korisno korisnicima
		tv = (TextView) dialog.findViewById(R.id.tvProgress);

		// Deklariranje klizaÄa, odnosno seekbara
		sb = (SeekBar) dialog.findViewById(R.id.sbDebljina);

		// Postavlja se listener za osluĹĄkivanje promjene na klizaÄu. Potrebna nam je samo prva metoda, a ove dvije nam ne trebaju, al moraju bit definirane
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// Postavljamo u textview trenutnu vrijednost na klizaÄu
				tv.setText(Integer.toString(progress));
				// Postavljeno je da je debljina jednaka trenutnoj vrijednosti na klizaÄu
				debljina = progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

	}

	//////// POSTAVLJANJE 2. DIALOGA (ZA BOJU) ////////
	public void postaviDialogZaBoju() {
		// Postupak ĹĄto se samog dialoga je identiÄan kao i u proĹĄloj metodi. Jedino su sada drugaÄiji elementi i drugaÄiji xml fajl
		final Dialog dialog = new Dialog(kontekst);

		// Sada se poziva dialogboja.xml koji se isto nalazi u /res/layout/ folderu
		dialog.setContentView(R.layout.dialogboja);
		dialog.setTitle("Pick a colour");

		// Ovaj textView je sluĹžio meni za debugiranje, a sad se ni ne prikazuju, tako da to slobodno ukloni
		tv = (TextView) dialog.findViewById(R.id.tvRadio);

		// Opet treba na isti naÄin definirat gumb za potvrdu
		Button ok = (Button) dialog.findViewById(R.id.bBojaOK);

		// Novi element. Ovdje je besmisleno koristit klizaÄ za boju. Sad se koristi tzv. grupa "radio buttona". 
		final RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.rgRadio);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// S ovime saznajemo ID trenutno oznaÄenog radio buttona. ID-ovi su 0 (crvena), 1 (plava) i 2 (zelena)
				int id = rg.getCheckedRadioButtonId();
				
				switch(id){
				case R.id.rbCrvena:
					CrtanjeView.boja.setColor(Color.RED);
					CrtanjeView.putanja = new mojaPutanja(new Paint(CrtanjeView.boja));
			        CrtanjeView.paths.add(CrtanjeView.putanja);
					break;
				case R.id.rbPlava:
					CrtanjeView.boja.setColor(Color.BLUE);
					CrtanjeView.putanja = new mojaPutanja(new Paint(CrtanjeView.boja));
			        CrtanjeView.paths.add(CrtanjeView.putanja);
					break;
				case R.id.rbZelena:
					CrtanjeView.boja.setColor(Color.BLACK);
					CrtanjeView.putanja = new mojaPutanja(new Paint(CrtanjeView.boja));
			        CrtanjeView.paths.add(CrtanjeView.putanja);
					break;
				}

				// Naravno, treba i ukloniti dialog nakon ĹĄto je potvrÄen odabir
				tv.setText(Integer.toString(id));
				dialog.dismiss(); //lol
			}
		});

		// Opet prikaz dialoga
		dialog.show();

	}

}