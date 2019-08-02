package com.stackroute.muzix.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.stackroute.muzix.model.Track;
import com.stackroute.muzix.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class TrackServiceImpl implements TrackService {

	TrackRepository trackRepository;

	@Autowired
	public TrackServiceImpl(TrackRepository trackRepository) {
		this.trackRepository = trackRepository;
	}

	@Override
	public boolean saveTrack(Track track) {
			trackRepository.save(track);
			return true;
	}

	@Override
	public boolean deleteTrack(int id) {
			trackRepository.deleteById(id);
			return true;
	}

	@Override
	public List<Track> getAllTracks() {
		List<Track> trackList = trackRepository.findAll();
		return trackList;
	}

	@Override
	public boolean updateTrack(int id, Track track) {
			if(trackRepository.existsById(id) == true){
				trackRepository.save(track);
				return true;
			}
		return false;
	}

	@Override
	public void getTopTracks() {

//	    instantiation

			RestTemplate restTemplate=new RestTemplate();
			String ResourceUrl
					= "http://ws.audioscrobbler.com/2.0/?method=tag.gettoptracks&tag=disco&api_key=4326c000f7df7c81681da5052adc2cf3&format=json";
			ResponseEntity<String> response
					= restTemplate.getForEntity(ResourceUrl, String.class);
			//To use object mapper
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode root=null;
			try
			{
				//To read the response body
				root = objectMapper.readTree(response.getBody());
				//To store the JSON array
				ArrayNode arrayNode=(ArrayNode) root.path("tracks").path("track");

				//Iterating through each JSON object
				for(int i=0;i<arrayNode.size();i++)
				{
					Track track=new Track();
					//To set the id of the tracks
					track.setId(i+1);
					//For the name of the track
					track.setName(arrayNode.get(i).path("name").asText());
					//For the artist name of the track
					track.setComment(arrayNode.get(i).path("artist").path("name").asText());

					trackRepository.save(track);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}



		}

  @Override
  public Optional<Track> getTrackById(int id) {
    Optional<Track> track = null;
	  if(trackRepository.existsById(id))
    {
     track=trackRepository.findById(id);
    }
    return track;
  }
}

