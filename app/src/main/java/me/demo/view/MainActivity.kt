package me.demo.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.ftpos.library.smartpos.emv.CandidateAIDInfo
import com.ftpos.library.smartpos.errcode.ErrCode
import com.ftpos.library.smartpos.util.EncodeConversionUtil
import com.jirui.logger.Logger
import me.demo.enums.TransactionType
import me.dvabi.terminal.databinding.ActivityMainBinding

class MainActivity : BaseEmvActivity() {

    private lateinit var binding: ActivityMainBinding
    private val transactionType = TransactionType.PURCHASE

    private val amount = "5"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startBtn.setOnClickListener {
////            startTransaction(amount, transactionType)
//
//            var dataOutputStream : DataOutputStream? = null
//            var socket : Socket? = null;
//
//            try {
//                socket = Socket("195.66.0.106", 5555);
//                dataOutputStream = new DataOutputStream(socket.getOutputStream());
//                dataOutputStream.writeUTF("on");
//            } catch (UnknownHostException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
        }
    }



    override fun cancelTransaction() {
        Toast.makeText(this, "otkazana transakcija", Toast.LENGTH_SHORT).show()
    }

    //ovo je samo u slucaju da chip na kartici ima vise aplikacija
    override fun doAppSelect(list: List<CandidateAIDInfo>?) {
        if (list.isNullOrEmpty()) return
        Handler(Looper.getMainLooper())
            .post {
                val listApp: MutableList<String?> = ArrayList()
                //Get the application name that needs to be displayed from CandidateAID
                for (i in list.indices) {
                    listApp.add(
                        EncodeConversionUtil.EncodeConversion(
                            list[i].applicationLabel_tag50,
                            list[i].codeTableIndex_tag9F11
                        )
                    )
                }

                if (list.size > 1) {
                    Toast.makeText(this, "more than 1 app", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "only 1 app", Toast.LENGTH_SHORT).show()
                }
            }
    }

    //ovo treba da se zove kad se zavrsi transakcija
    override fun doEndProcess(code: Int, data: String?) {
        Toast.makeText(this, "zavrrsena transakcija", Toast.LENGTH_SHORT).show()

        Logger.d("onEndProcess: 0x" + Integer.toHexString(code) + " - " + ErrCode.toString(code))
        //            iemv.respondEvent(null);
//        Logger.d("Trans Card Type: 0x%02X", iemv.transCardtype)

        //if ("F360".equals(Build.MODEL)) {
        //led.ledDefault();
//        led.ledCardIndicator(0x00, 0x00, 0x00, 0x00)
//        led.ledCardIndicator(0x00, 0x00, 0x00, 0x00)
    }
}