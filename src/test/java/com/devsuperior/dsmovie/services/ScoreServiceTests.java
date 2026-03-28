package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService scoreService;

	@Mock
	private UserService userService;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private ScoreRepository scoreRepository;

	private Long existingMovieId, nonExistingMovieId;
	private MovieEntity movie;
	private MovieDTO movieDTO;
	private ScoreEntity score;
	private ScoreDTO scoreDTO;
	private UserEntity user;

	@BeforeEach
	void setUp() throws Exception {

		existingMovieId = 1L;
		nonExistingMovieId = 2L;

		user = UserFactory.createUserEntity();

		movie = MovieFactory.createMovieEntity();
		movieDTO = new MovieDTO(movie);

		score = ScoreFactory.createScoreEntity();
		scoreDTO = new ScoreDTO(score);

		Mockito.when(userService.authenticated()).thenReturn(user);
		Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movie));
		Mockito.when(scoreRepository.saveAndFlush(any())).thenAnswer(invocation -> {
			ScoreEntity s = invocation.getArgument(0);
			movie.getScores().add(s);
			return s;
		});
		Mockito.when(movieRepository.save(any())).thenReturn((movie));

		Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());

	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {

		MovieDTO result = scoreService.saveScore(scoreDTO);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), movie.getId());
		Assertions.assertEquals(result.getScore(), movie.getScore());
		Assertions.assertEquals(result.getCount(), movie.getCount());

	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {

		ScoreDTO scoreDTO = new ScoreDTO(nonExistingMovieId, 4.5);

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			scoreService.saveScore(scoreDTO);
		});

	}
}
