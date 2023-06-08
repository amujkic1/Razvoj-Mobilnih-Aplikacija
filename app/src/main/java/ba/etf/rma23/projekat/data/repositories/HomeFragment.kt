package ba.etf.rma23.projekat.data.repositories

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ba.etf.rma23.projekat.MainActivity
import ba.etf.rma23.projekat.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var gamesListAdapter: GameListAdapter
    var allGamesList : List<Game> = ArrayList<Game>()
    private lateinit var searchText: EditText
    private lateinit var selected: Game
    private lateinit var lastOpened: Game
    private lateinit var searchButton: Button
    private lateinit var favoritesButton: Button
    private lateinit var sortButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_home, container, false)
        gamesList = view.findViewById(R.id.game_list)
        gamesList.layoutManager = GridLayoutManager(activity, 1)
        gamesListAdapter = GameListAdapter(arrayListOf()){ game -> showGameDetails(game) }
        gamesList.adapter = gamesListAdapter

        searchText = view.findViewById(R.id.search_query_edittext)
        searchButton = view.findViewById(R.id.search_button)
        favoritesButton = view.findViewById(R.id.favorites_button)
        sortButton = view.findViewById(R.id.sort_button)

        searchButton.setOnClickListener{
            getGamesByName(searchText.text.toString())
            //getGamesContainingString(searchText.text.toString())
        }

        favoritesButton.setOnClickListener{
            getSavedGames()
        }

        sortButton.setOnClickListener {
            sortGames()
        }

        //korisnik vidi sačuvane igrice kad tek otvori app
        getSavedGames()

        return view
    }


    private fun getGamesContainingString(query: String){
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            val result = AccountGamesRepository.getGamesContainingString(query)
            when(result){
                is List<Game> -> onSuccess(result)
                else -> onError()
            }
        }
    }

    private fun getGamesByName(query: String) {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            val result = GamesRepository.getGamesByName(query)
            when(result){
                is List<Game> -> onSuccess(result)
                else -> onError()
            }
        }
    }

    private fun getSavedGames(){
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            val result = AccountGamesRepository.getSavedGames()
            when(result){
                is List<Game> -> onSuccess(result)
                else -> onError()
            }
        }
    }

    private fun sortGames(){
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            val result = GamesRepository.sortGames()
            when(result){
                is List<Game> -> onSuccess(result)
                else -> onError()
            }
        }
    }

    private fun getGamesSafe(query: String){
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            val result = GamesRepository.getGamesSafe(query)
            when(result){
                is List<Game> -> onSuccess(result)
                else -> onError()
            }
        }
    }

    fun onSuccess(games: List<Game>){
        val toast = Toast.makeText(context, "Games found", Toast.LENGTH_SHORT)
        toast.show()
        gamesListAdapter.updateGames(games)
    }

    fun onError(){
        val toast = Toast.makeText(context, "Search error", Toast.LENGTH_SHORT)
        toast.show()
    }

    private fun showGameDetails(game: Game){
        lastOpened = getGameByTitle(game.name)
        if(resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE)
            findNavController().navigate(R.id.gameDetailsItem, Bundle().apply { putString("game_title", game.name) })
        else
            MainActivity.navControllerDetails.navigate(R.id.gameDetailsItem, Bundle().apply { putString("game_title", game.name) })
    }

    private fun getGameByTitle(name: String?): Game {
        val game = allGamesList.find { it.name == name }
        return game?:Game(1, "name","", "", 0.1, "", "", "", "", "", "",
            emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
    }

    companion object {
        lateinit var gamesList: RecyclerView
    }

}