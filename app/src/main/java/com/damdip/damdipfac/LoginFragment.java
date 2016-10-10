package com.damdip.damdipfac;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
//import com.facebook.login.LoginClient;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class LoginFragment extends Fragment {

	private CallbackManager callbackManager;

	private TextView textView;
	private ProfilePictureView profilePictureView;
	private TextView IDUsuario;
	private TextView NomeUsuario;
	private TextView PrimeiroNome;
	private TextView NomeDoMeio;
	private TextView UltimoNome;

	private Button postImageBtn;

	private AccessTokenTracker accessTokenTracker;
	private ProfileTracker profileTracker;
	private LoginManager loginManager;
	private Bitmap bitmap;

	private EditText edt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize facebook SDK.
		FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

		// Create a callbackManager to handle the login responses.
		callbackManager = CallbackManager.Factory.create();

		//Inserido para permitir adicionar figuras
		List<String> permissionNeeds = Arrays.asList("publish_actions");
		loginManager = LoginManager.getInstance();
		loginManager.logInWithPublishPermissions(this,permissionNeeds);
		loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {

			}

			@Override
			public void onCancel() {System.out.println("onCancel");

			}

			@Override
			public void onError(FacebookException error) {
				System.out.println("onCancel");
				Log.v("LoginActivity",error.getCause().toString());

			}
		});

		//Até aqui



		accessTokenTracker= new AccessTokenTracker() {
			@Override
			protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
				Toast.makeText(getActivity(), "AccessToken changed", Toast.LENGTH_SHORT).show();
			}
		};

		profileTracker = new ProfileTracker() {
			@Override
			protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
				displayMessage(newProfile);
			}
		};

		accessTokenTracker.startTracking();
		profileTracker.startTracking();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_login, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		edt = (EditText) view.findViewById(R.id.editText1);
		textView = (TextView) view.findViewById(R.id.textView);


		//LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
		//profilePictureView = (ProfilePictureView) view.findViewById(R.id.profilePicture);
		IDUsuario = (TextView) view.findViewById(R.id.idusuario);
		NomeUsuario= (TextView) view.findViewById(R.id.nomeusuario);
		PrimeiroNome= (TextView) view.findViewById(R.id.primeironome);
		NomeDoMeio= (TextView) view.findViewById(R.id.nomedomeio);
		UltimoNome= (TextView) view.findViewById(R.id.ultimonome);

		profilePictureView = (ProfilePictureView) view.findViewById(R.id.profilePicture);

		postImageBtn = (Button) view.findViewById(R.id.post_image);

		postImageBtn.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View view) {
				sharePhotoToFacebook();
			}
		});

		customizeLoinButton(view);
	}

/**	public void postImage() {


		if (checkPermissions()) {
			Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			LoginClient.Request uploadRequest = LoginClient.Request.newUploadPhotoRequest(
					Session.getActiveSession(), img, new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							Toast.makeText(LoginFragment.this,
									"Photo uploaded successfully",
									Toast.LENGTH_LONG).show();
						}
					});
			uploadRequest.executeAsync();
		} else {
			requestPermissions();
		}
	}
*/
	private void customizeLoinButton(View view) {
		LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
		loginButton.setReadPermissions("user_friends","publish_actions");
		loginButton.setFragment(this);
		loginButton.registerCallback(callbackManager, callback);
		//AccessToken.getCurrentAccessToken().getPermissions();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Call callbackManager.onActivityResult to pass login result to the LoginManager via callbackManager.
		callbackManager.onActivityResult(requestCode, resultCode, data);

		/*/Acessar fotos
		InputStream stream = null;
		if (requestCode == 1 && resultCode == RESULT_OK) {
			try {
				if (bitmap != null) {
					bitmap.recycle();
				}
				stream = getContentResolver().openInputStream(data.getData());
				bitmap = BitmapFactory.decodeStream(stream);
				imgl.setImageBitmap(bitmap);
			}
			catch(FileNotFoundException e) {
				e.printStackTrace();
			}
			finally {
				if (stream != null)
					try {
						stream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}

		}*/

	}

	private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
		@Override
		public void onSuccess(LoginResult loginResult) {

			//publishImage();
			publishImage();
			sharePhotoToFacebook();
			AccessToken accessToken = loginResult.getAccessToken();
			Toast.makeText(getActivity(), "AccessToken = " + accessToken, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getActivity(), "User Cancelled login", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(FacebookException e) {
			Toast.makeText(getActivity(), "Error occurred while login", Toast.LENGTH_SHORT).show();
		}
	};



	private void displayMessage(Profile profile){
		if(profile != null) {
			//String name = (profile != null) ? profile.getName() : "User not logged in";

			textView.setText(profile.getName());
			IDUsuario.setText(profile.getId());
			NomeUsuario.setText(profile.getName());
			PrimeiroNome.setText(profile.getFirstName());
			NomeDoMeio.setText(profile.getMiddleName());
			UltimoNome.setText(profile.getLastName());

			profilePictureView.setProfileId(profile.getId());


		}
	}

	@Override
	public void onStop() {
		super.onStop();
		accessTokenTracker.stopTracking();
		profileTracker.stopTracking();
	}

	@Override
	public void onResume() {
		super.onResume();
		Profile profile = Profile.getCurrentProfile();
		displayMessage(profile);
	}


	public void carregarGaleria(){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent,1);
	}

	private void publishImage() {
		System.out.println("Publish Image");

		Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);//bitmap

		SharePhoto photo = new SharePhoto.Builder()
				.setBitmap(image)
				.setCaption("Give me my codez or I will ... you know, do that thing you don't like!")
				.build();
		SharePhotoContent content = new SharePhotoContent.Builder()
				.addPhoto(photo)
				.build();

		ShareApi.share(content, null);

	}

	private void sharePhotoToFacebook(){
		//Bitmap image = ...
		//SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();
		//SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
		System.out.println("Share Photo !!!!");

		Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);


		System.out.println("Share Photo !!!!");

		SharePhoto photo = new SharePhoto.Builder()
				.setBitmap(image)
				.setCaption("Give me my codez or I will ... you know, do that thing you don't like!")
				.build();
		System.out.println("Share Photo !!!!");

		SharePhotoContent content = new SharePhotoContent.Builder()
				.addPhoto(photo)
				.build();
		System.out.println("Share Photo !!!!");

		ShareApi.share(content, null);

	}

	////////Aqui inserido
	private int cont = 0;
	private String corpo;

	public void galleryButtonClick(View v) {
		// apagaTemp();

		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, 0);

		cont++;
	}

	/* Evento de clique do botão para atualizar o status
	public void updateStatusClick(View v) {
//		EditText edt = (EditText) findViewById(R.id.editText1);

		if ((edt.getText().toString().trim().equals(""))
				|| (edt.getText().toString() == null)) {

			Toast toast =Toast.makeText(getContext(), "Ops!!!, Digite algo para seus amigos...", Toast.LENGTH_LONG);
			toast.show();
		} else if ((edt.getText().toString().trim().equals(""))
				|| (edt.getText().toString() == null) || cont > 0) {

			corpo = edt.getText().toString();

			sendPhoto(edt.getText().toString());

		} else if (cont == 0) {

			corpo = edt.getText().toString();

			updateStatus(edt.getText().toString());


		}

		edt.setText("Aguarde enquando publicamos o seu post, isso pode demorar um pouco...");
		edt.setEnabled(false);

	}

	/*private RequestListener requestListener = new RequestListener() {
		public void onMalformedURLException(MalformedURLException e,
											Object state) {
			showToast("URL mal formada");
		}

		public void onIOException(IOException e, Object state) {
			showToast("Problema de comunicação");
		}

		public void onFileNotFoundException(FileNotFoundException e,
											Object state) {
			showToast("Recurso não existe");
		}

		public void onFacebookError(FacebookError e, Object state) {
			showToast("Erro no Facebook: " + e.getLocalizedMessage());
		}

		public void onComplete(String response, Object state) {

			showToast("Publicação realizada com sucesso");

		}
	};
*/
	/* Método que efetivamente atualiza o status
	private void updateStatus(String status) {
		AsyncFacebookRunner runner = new AsyncFacebookRunner(facebook);

		Bundle params = new Bundle();
		params.putString("message", status);
		runner.request("me/feed", params, "POST", requestListener, null);
	}

	// Método que efetivamente atualiza o status com imagem
	private void sendPhoto(String status) {
		AsyncFacebookRunner runner = new AsyncFacebookRunner(facebook);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		image.compress(Bitmap.CompressFormat.PNG, 100, baos);

		byte[] bytes = baos.toByteArray();

		Bundle params = new Bundle();
		params.putByteArray("picture", bytes);
		params.putString("message", status);

		runner.request("me/photos", params, "POST", requestListener, null);
	}

	/*
	private void showToast(final String s) {
		final Context ctx = DevmediaFacebookActivity.this;
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void saveAccessToken() {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(ACCESS_TOKEN, facebook.getAccessToken());
		editor.putLong(ACCESS_EXPIRES, facebook.getAccessExpires());
		editor.commit();
	}

	private void loadAccessToken() {
		String access_token = prefs.getString(ACCESS_TOKEN, null);
		long expires = prefs.getLong(ACCESS_EXPIRES, 0);
		if (access_token != null) {
			facebook.setAccessToken(access_token);
		}
		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}
	}
	*/
/*
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (cont == 0) {
			// A API do Facebook exige essa chamada para
			// concluir o processo de login.
			facebook.authorizeCallback(requestCode, resultCode, data);
		} else {

			if (resultCode == RESULT_OK) {
				switch (requestCode) {
					case 0:

						Uri selectedImage = data.getData();
						String[] filePathColumn = { MediaStore.Images.Media.DATA };

						Cursor cursor = getContentResolver().query(selectedImage,
								filePathColumn, null, null, null);
						cursor.moveToFirst();

						int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
						String filePath = cursor.getString(columnIndex); // file
						// path
						// of
						// selected
						// image
						cursor.close();

						Bitmap yourSelectedImage = BitmapFactory
								.decodeFile(filePath);

						imageView1.setImageBitmap(yourSelectedImage);

						image = yourSelectedImage;

						break;

					case 1:
						fotoTirada = BitmapFactory.decodeFile(caminhoFoto
								.getAbsolutePath());
						imageView1.setImageBitmap(fotoTirada);
						break;
				}
			}

		}
	}
*/



}