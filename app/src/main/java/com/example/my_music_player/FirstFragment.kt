package com.example.my_music_player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.example.my_music_player.databinding.FragmentFirstBinding

/**
 * A [Fragment] subclass as the first destination in the navigation
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and on DestroyView.
    private val binding get() = _binding!!

    private var updateSeekbar = false

    private lateinit var runnable: Runnable
    private lateinit var handler: Handler

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up UI for the fragment
        // Add listener for example

        val mediaPlayer = this.createMediaPlayer()
        this.createSeekBar(mediaPlayer)
        this.addCompletionListener(mediaPlayer)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createMediaPlayer(): MediaPlayer {
        val mediaPlayer = MediaPlayer.create(activity, R.raw.music)
        binding.playButton.setOnClickListener{
            if(!mediaPlayer.isPlaying) {
                mediaPlayer.start()
                binding.playButton.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)

                this.updateSeekbar = true
                handler.postDelayed(runnable, 1000)
            }
            else
            {
                mediaPlayer.pause()
                binding.playButton.setImageResource(R.drawable.ic_baseline_play_circle_outline_24)

                this.updateSeekbar = false
            }
        }

        return mediaPlayer
    }

    private fun createSeekBar(mediaPlayer: MediaPlayer) {
        binding.seekbar.progress = 0
        binding.seekbar.max      = mediaPlayer.duration

        binding.seekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, position: Int, changed: Boolean) {
                if(changed) {
                    mediaPlayer.seekTo(position)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        this.runnable = Runnable {
            if(mediaPlayer.isPlaying) {
                binding.seekbar.progress = mediaPlayer.currentPosition
            }

            if(this.updateSeekbar) {
                handler.postDelayed(runnable, 1000)
            }
        }

        this.handler = Handler(Looper.getMainLooper())
    }

    private fun addCompletionListener(mediaPlayer: MediaPlayer) {
        mediaPlayer.setOnCompletionListener {
            binding.playButton.setImageResource(R.drawable.ic_baseline_play_circle_outline_24)
            binding.seekbar.progress = 0

            this.updateSeekbar = false

            mediaPlayer.stop()
            mediaPlayer.prepare()
        }
    }
}