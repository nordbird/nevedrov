package ru.nordbird.developerslife.ui.main

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import ru.nordbird.developerslife.R
import ru.nordbird.developerslife.data.model.CardItem
import ru.nordbird.developerslife.databinding.FragmentMainBinding
import ru.nordbird.developerslife.utils.Resource
import ru.nordbird.developerslife.utils.Status

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pageType = when (arguments?.getInt(ARG_SECTION_NUMBER) ?: 0) {
            0 -> PageType.LATEST
            1 -> PageType.TOP
            2 -> PageType.HOT
            else -> PageType.LATEST
        }
        pageViewModel =
            ViewModelProvider(this, PageViewModelFactory(pageType)).get(PageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        pageViewModel.getCard().observe(this, {
            updateUI(it)
        })

        pageViewModel.getCardIndex().observe(this, {
            updatePrevButton(it > 0)
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.ibNext.setOnClickListener { pageViewModel.nextCard() }
        binding.ibPrev.setOnClickListener { pageViewModel.prevCard() }
        binding.btnReloadCard.setOnClickListener { pageViewModel.reloadCard() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI(response: Resource<CardItem>) {
        Glide.with(binding.root).clear(binding.ivCardGif)

        if (response.status == Status.ERROR) {
            binding.llNetworkError.visibility = View.VISIBLE
            binding.cvCard.visibility = View.GONE
            binding.llButtons.visibility = View.GONE
        } else {
            binding.llNetworkError.visibility = View.GONE
            binding.cvCard.visibility = View.VISIBLE
            binding.llButtons.visibility = View.VISIBLE

            if (response.status == Status.LOADING) {
                binding.pbCardLoading.visibility = View.VISIBLE
                binding.ivCardGif.visibility = View.GONE
                binding.tvCardTitle.visibility = View.GONE
            } else {
                binding.pbCardLoading.visibility = View.GONE
                binding.ivCardGif.visibility = View.VISIBLE
                binding.tvCardTitle.visibility = View.VISIBLE

                val card = response.data ?: return
                if (card.gifURL.isNotBlank()) {
                    Glide.with(binding.root).load(card.gifURL).into(binding.ivCardGif)
                }
                binding.tvCardTitle.text = card.description
            }

        }

    }

    private fun updatePrevButton(enabled: Boolean) {
        binding.ibPrev.isEnabled = enabled
        binding.ibPrev.isClickable = enabled
        binding.ibPrev.imageAlpha = if (enabled) 255 else 50

        val filter: ColorFilter? = if (!enabled) {
            PorterDuffColorFilter(
                resources.getColor(R.color.grey_200, activity?.theme),
                PorterDuff.Mode.SRC_IN
            )
        } else null

        val icon = resources.getDrawable(R.drawable.ic_round_replay_48, activity?.theme)

        binding.ibPrev.background.colorFilter = filter
        binding.ibPrev.setImageDrawable(icon)
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}