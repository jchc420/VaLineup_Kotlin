package com.jchc.practica2.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.jchc.practica2.R
import com.jchc.practica2.application.EsportsRFApp
import com.jchc.practica2.data.OrgRepository
import com.jchc.practica2.data.remote.model.OrgDetailDto
import com.jchc.practica2.databinding.FragmentOrgDetailBinding
import com.jchc.practica2.utils.Constants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val ORG_ID = "org_id"

class OrgDetailFragment: Fragment() {

    private var _binding: FragmentOrgDetailBinding? = null
    private val binding get() = _binding!!

    private var org_id: String? = null

    private lateinit var repository: OrgRepository

    //para el player de youtube
    private lateinit var youTubePlayerView: YouTubePlayerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { args ->
            org_id = args.getString(ORG_ID)
            Log.d(Constants.LOGTAG, "ID RECIBIDO: $org_id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Inflate the layout for this fragment
        _binding = FragmentOrgDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = (requireActivity().application as EsportsRFApp).repository

        youTubePlayerView = binding.ytPlayerView

        lifecycleScope.launch {
            org_id?.let { id ->
                val call: Call<OrgDetailDto> = repository.getOrgDetail(id)
                call.enqueue(object : Callback<OrgDetailDto> {
                    override fun onResponse(
                        p0: Call<OrgDetailDto>,
                        response: Response<OrgDetailDto>
                    ) {
                        binding.apply {
                            pbLoading.visibility = View.INVISIBLE
                            tvTitle.text = response.body()?.title

                            Glide.with(requireActivity())
                                .load(response.body()?.image)
                                .into(ivImage)

                            //lifecycle.addObserver(youTubePlayerView)
                            youTubePlayerView = binding.ytPlayerView

                            val characters = resources.getStringArray(R.array.characters)
                            val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, characters)
                            //binding.autoCompleteTextView.setAdapter(arrayAdapter)
                            val actv: AutoCompleteTextView = binding.autoCompleteTextView
                            actv.setAdapter(arrayAdapter)
                            youTubePlayerView.addYouTubePlayerListener(object: AbstractYouTubePlayerListener(){
                                override fun onReady(youTubePlayer: YouTubePlayer) {
                                    actv.setOnItemClickListener { parent, view, position, id ->
                                        if (characters[position] == "Sova"){
                                            binding.ytPlayerView.visibility = View.VISIBLE
                                            var videoUrl: String = response.body()?.sovaLnp.toString()
                                            youTubePlayer.loadVideo(videoUrl, 0f)
                                        } else if (characters[position] == "KAYO"){
                                            binding.ytPlayerView.visibility = View.VISIBLE
                                            var videoUrl: String = response.body()?.kayoLnp.toString()
                                            youTubePlayer.loadVideo(videoUrl, 0f)
                                        } else if (characters[position] == "Gekko"){
                                            binding.ytPlayerView.visibility = View.VISIBLE
                                            var videoUrl: String = response.body()?.gekkoLnp.toString()
                                            youTubePlayer.loadVideo(videoUrl, 0f)
                                        } else if (characters[position] == "Viper"){
                                            binding.ytPlayerView.visibility = View.VISIBLE
                                            var videoUrl: String = response.body()?.viperLnp.toString()
                                            youTubePlayer.loadVideo(videoUrl, 0f)
                                        } else if (characters[position] == "Killjoy"){
                                            binding.ytPlayerView.visibility = View.VISIBLE
                                            var videoUrl: String = response.body()?.kjLnp.toString()
                                            youTubePlayer.loadVideo(videoUrl, 0f)
                                        } else if (characters[position] == "Fade"){
                                            binding.ytPlayerView.visibility = View.VISIBLE
                                            var videoUrl: String = response.body()?.fadeLnp.toString()
                                            youTubePlayer.loadVideo(videoUrl, 0f)
                                        } else if (characters[position] == "Brimstone"){
                                            binding.ytPlayerView.visibility = View.VISIBLE
                                            var videoUrl: String = response.body()?.brimLnp.toString()
                                            youTubePlayer.loadVideo(videoUrl, 0f)
                                        } else {
                                            binding.ytPlayerView.visibility = View.INVISIBLE
                                        }
                                    }

                                }
                            })
                        }
                    }

                    override fun onFailure(p0: Call<OrgDetailDto>, p1: Throwable) {
                        //Aquí se maneja el error sin conexión (recomendado:
                        //un fragment con un mensaje de error y un botón para reintentar la conexión
                        binding.pbLoading.visibility = View.INVISIBLE
                    }

                })
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(orgId: String) =
            OrgDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ORG_ID, orgId)
                }
            }
    }
}
