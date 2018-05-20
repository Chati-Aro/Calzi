package app.chati.codecs.code.calzi;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressWarnings("all")
public class Calzi_Home extends AppCompatActivity {

    //Variable Shit
    ImageView helper; //To temporarily store images
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Setting stuff up
        super.onCreate(savedInstanceState);
        //Fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_calzi__home);
        spf = getSharedPreferences("com.app.calzi", MODE_PRIVATE);
        //Setup (more)
        setup();
    }

    SharedPreferences spf = null;
    protected void setup(){
        //To check first run
        if(spf.getBoolean("firstTime", true)){
            introScreen();
        }
        spf.edit().putBoolean("firstTime", false);

        Button btn = (Button) (findViewById(R.id.button2));
        btn.setVisibility(View.INVISIBLE);
    }

    protected AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ;

    protected void introScreen(){
        ImageView mainOne = findViewById(R.id.imageView);
        mainOne.setImageResource(R.raw.intro_back);

        animate((TextView)findViewById(R.id.textView1), 100);
        animate((TextView)findViewById(R.id.textView2), 1000);

        mainOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Calzi_Home.this);

                // Set a title for alert dialog
                builder.setTitle("Note:");

                // Ask the final question
                builder.setMessage("On the next screen, you will be prompted to take a picture of the person who is going " +
                        "to use this app. This image will be used: " +
                        "\n 1) As the wallpaper of this app \n 2) For Face Recognition purpose");

                // Set click listener for alert dialog buttons
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firstTime();
                    }
                };

                // Set the alert dialog yes button click listener
                builder.setPositiveButton("Yes", dialogClickListener);

                AlertDialog dialog = builder.create();
                // Display the alert dialog on interface
                dialog.show();
            }
        });
    }

    int REQUEST_CODE = 1;
    private void firstTime(){
        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(camIntent.resolveActivity(getPackageManager())!= null){
            startActivityForResult(camIntent, REQUEST_CODE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle theWholeThing = data.getExtras();
        Bitmap onlyTheRequiredShit = (Bitmap) theWholeThing.get("data");
        helper = new ImageView(this);
        helper.setImageBitmap(onlyTheRequiredShit);

        TextView txtView1 = findViewById(R.id.textView1);
        TextView txtView2 = findViewById(R.id.textView2);


        final ImageView mainOne = findViewById(R.id.imageView);
        mainOne.setImageBitmap(fastblur(onlyTheRequiredShit, 9.0f, 25));
        ConstraintLayout constraintLayout = findViewById(R.id.parentPanel);
        constraintLayout.removeAllViews();
        constraintLayout.addView(mainOne);

        setupForWork();
    }

    protected void setupForWork(){

    }

    protected void animate(TextView txtView, int time){
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
        txtView.startAnimation(fadeIn);
        fadeIn.setDuration(1200);
        fadeIn.setFillAfter(true);
        fadeIn.setStartOffset(time+fadeIn.getStartOffset());
    }
    protected void animate(Button txtView){
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
        txtView.startAnimation(fadeIn);
        fadeIn.setDuration(1200);
        fadeIn.setFillAfter(true);
        fadeIn.setStartOffset(900+fadeIn.getStartOffset());
    }
    //////////////////////////////////Internet Shit://///////////////////////////////
    Bitmap fastblur(Bitmap sentBitmap, float scale, int radius) {

        int width = Math.round(sentBitmap.getWidth() * scale);
        int height = Math.round(sentBitmap.getHeight() * scale);
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }
}

