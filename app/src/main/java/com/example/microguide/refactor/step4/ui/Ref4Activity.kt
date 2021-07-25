package com.example.microguide.refactor.step4.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.microguide.extensions.startActivity
import com.example.microguide.refactor.step4.presentation.Ref4ViewModel
import dagger.hilt.android.AndroidEntryPoint

// обращаю внимание, что наследуемся от ComponentActivity, а не от AppCompatActivity
// после полного переезда на компост можно выкинуть нахер appcompat либу и уменьшить размер своей апкшки
// я кста пока хз как показать снэкбар, поэтому без него)
@AndroidEntryPoint
class Ref4Activity : ComponentActivity() {

    companion object {

        fun start(context: Context) {
            context.startActivity<Ref4Activity>()
        }
    }

    val viewModel: Ref4ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val screenState by viewModel.screenState.collectAsState()

                Ref4Screen(
                    screenState = screenState,
                    onUpdateComments = viewModel::updateComments,
                )
            }
        }
    }
}