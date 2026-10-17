package com.wanmad.pos.test

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService

/**
 * MainActivity: Tela principal do aplicativo de testes voltado para terminais POS (Point of Sale).
 * Contém funcionalidades para exibir informações do hardware, emitir sons (beeps), vibrar o dispositivo
 * e disparar a impressão de comprovantes.
 */
class MainActivity : AppCompatActivity() {

    // Declaração do componente de texto para exibir status e mensagens na tela
    private lateinit var output: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Define o layout da Activity a partir do arquivo XML correspondente
        setContentView(R.layout.activity_main)
        
        // Inicializa a referência do TextView de saída
        output = findViewById(R.id.tvOutput)

        // Configura os ouvintes de cliques (Listeners) para cada botão da interface
        findViewById<View>(R.id.btnDeviceInfo).setOnClickListener { showDeviceInfo() }
        findViewById<View>(R.id.btnBeep).setOnClickListener { playBeep() }
        findViewById<View>(R.id.btnVibrate).setOnClickListener { vibrateOnce() }
        findViewById<View>(R.id.btnPrint).setOnClickListener { printReceipt() }
    }

    /**
     * Coleta e exibe informações detalhadas sobre o hardware e o sistema operacional Android
     * do dispositivo atual (muito útil para homologação em diferentes terminais POS).
     */
    private fun showDeviceInfo() {
        // Constrói uma string formatada contendo dados de compilação e sistema
        val info = buildString {
            appendLine("Fabricante: ${Build.MANUFACTURER}")
            appendLine("Modelo: ${Build.MODEL}")
            appendLine("Marca: ${Build.BRAND}")
            appendLine("Dispositivo: ${Build.DEVICE}")
            appendLine("Android: ${Build.VERSION.RELEASE}")
            appendLine("SDK: ${Build.VERSION.SDK_INT}")
        }
        
        // Atualiza o TextView na tela com as informações coletadas
        output.text = info
        
        // Exibe um AlertDialog pop-up com as mesmas informações para melhor visualização
        AlertDialog.Builder(this)
            .setTitle("Informações do dispositivo")
            .setMessage(info)
            .setPositiveButton("OK", null)
            .show()
    }

    /**
     * Emite um sinal sonoro (beep) curto utilizando o gerador de tons do sistema Android.
     * Ideal para dar feedback sonoro em leituras de código de barras ou confirmações de vendas.
     */
    private fun playBeep() {
        // Cria um ToneGenerator associado ao canal de notificações com volume máximo (100)
        val tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
        
        // Inicia a execução do tom de beep padrão com duração de 200 milissegundos
        tone.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
        
        output.text = "Beep executado."
        
        // Agenda a liberação dos recursos de áudio do ToneGenerator após o término do som
        window.decorView.postDelayed({ tone.release() }, 350)
    }

    /**
     * Aciona o motor de vibração do dispositivo de forma controlada.
     * Trata a compatibilidade entre versões mais recentes do Android (Oreo+) e versões legadas.
     */
    @Suppress("DEPRECATION")
    private fun vibrateOnce() {
        // Obtém o serviço de vibração do sistema de forma segura
        val vibrator = getSystemService<Vibrator>()
        
        // Valida se o dispositivo realmente possui hardware de vibração
        if (vibrator?.hasVibrator() != true) {
            output.text = "Este dispositivo não possui vibrador."
            return
        }
        
        // Verifica a versão do SDK para usar a API recomendada de vibração
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0 (Oreo) ou superior: usa VibrationEffect com duração de 200ms e amplitude padrão
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            // Versões anteriores do Android: método legado que aceita apenas a duração em milissegundos
            vibrator.vibrate(200)
        }
        output.text = "Vibração executada."
    }

    /**
     * Prepara e envia um comprovante/cupom para o gerenciador de impressão do Android,
     * transformando o layout visual em um documento imprimível.
     */
    private fun printReceipt() {
        // Infla o layout XML do cupom (receipt_layout) para ser utilizado como View de impressão
        val receipt = (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(R.layout.receipt_layout, null)
            
        // Obtém o serviço gerenciador de impressão do sistema
        val printManager = getSystemService<PrintManager>() ?: return
        
        // Dispara o trabalho de impressão utilizando um adaptador customizado para a View
        printManager.print(
            "${getString(R.string.app_name)} - Cupom",
            ViewPrintAdapter(receipt),
            PrintAttributes.Builder().build()
        )
        output.text = "Janela de impressão aberta."
    }
}
