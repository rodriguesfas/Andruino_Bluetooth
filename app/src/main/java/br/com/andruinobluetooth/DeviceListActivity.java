package br.com.andruinobluetooth;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import me.drakeet.materialdialog.MaterialDialog;


public class DeviceListActivity extends ActionBarActivity {
    /* Debugging para LOGCAT */
    private static String TAG = "LOG DeviceListActivity";
    private static final boolean D = true;

    /* Envia endereço para AndruinoActivity */
    public static String EXTRA_DEVICE_ADDRESS = "device_addres";
    private AlertDialog.Builder alerta;
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private Toolbar mToolbar;
    private Toolbar mToolbarBotton;
    private MaterialDialog mMaterialDialog;

    /**
     * Set up on-click listener for the list (nicked this - unsure)
     */
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        /**
         * onItemClick
         * @param av
         * @param v
         * @param arg2
         * @param arg3
         */
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            mMaterialDialog = new MaterialDialog(new ContextThemeWrapper(DeviceListActivity.this, R.style.MyAlertDialog))
                    .setTitle("Bluetooth")
                    .setMessage("Conectando ao dispositivo, por favor aguarde..")
                    /*.setPositiveButton("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    })*/
                    .setNegativeButton("Cancelar", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                        }
                    });
            mMaterialDialog.show();

            // Obtem o endereço MAC do dispositivo, que você nos algoritimos 17 caracteres na View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Faza a intent começar a próxima activity, passando pelo extra o endereço MAC.
            Intent i = new Intent(DeviceListActivity.this, AndruinoActivity.class);
            i.putExtra(EXTRA_DEVICE_ADDRESS, address);
            startActivity(i);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle(R.string.app_nome);
        mToolbar.setSubtitle(R.string.selecione_dispositivo);
        mToolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(mToolbar);

        mToolbarBotton = (Toolbar) findViewById(R.id.inc_tb_bottom);
        mToolbarBotton.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent it = null;

                switch (menuItem.getItemId()) {
                    case R.id.action_facebook:
                        it = new Intent(Intent.ACTION_VIEW);
                        it.setData(Uri.parse("http://www.facebook.com"));
                        break;
                }
                startActivity(it);
                return true;
            }
        });
        mToolbarBotton.inflateMenu(R.menu.menu_bottom_device_list);
        mToolbarBotton.findViewById(R.id.iv_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DeviceListActivity.this, "Configurações", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * onResume
     */
    @Override
    public void onResume() {
        super.onResume();

        checkBTState();

        // Initialize array adapter for paired devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_device_name);

        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices and append to 'pairedDevices'
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // Add previosuly paired devices to the array
        if (pairedDevices.size() > 0) {
            //findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE); // make title viewable

            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }

        } else {
            String noDevices = getResources().getText(R.string.nao_pareado).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }
    }


    /**
     * checkBTState - Verifica Status de conectividade bluetooth no dispositivo.
     */
    private void checkBTState() {
        /* Verifica se o dispositivo possui Bluetooth, se está ligado ou sendo usado por outra app.. */
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBtAdapter == null) { /* Verifica a existencia da tecnologia bluetooth no dispositivo.. */
            Toast.makeText(getBaseContext(), "Dispositivo não suporta bluetooth!", Toast.LENGTH_SHORT).show();
        } else {
            if (mBtAdapter.isEnabled()) { // Verifica se o bluetooth está ativo..
                Toast.makeText(getBaseContext(), "Bluetooth ativo!", Toast.LENGTH_SHORT).show();
            } else { /* Se não estiver ativado, soliciata ao usuário a ativação.. */
                /* Prompt user to turn on Bluetooth */
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.menu_device_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * Handle action bar item clicks here.
         * The action bar will automatically handle clicks on the Home/Up button,
         * so long as you specify a parent activity in AndroidManifest.xml.
         */
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sobre) {
            startActivity(new Intent(this, SobreActivity.class));
        } else if (id == R.id.action_ajuda) {
            startActivity(new Intent(this, AjudaActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

} // FIM Class DeviceListActivity
