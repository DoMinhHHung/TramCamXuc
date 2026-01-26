package iuh.fit.se.tramcamxuc.modules.admin.service.impl;

import iuh.fit.se.tramcamxuc.modules.admin.dto.ChartData;
import iuh.fit.se.tramcamxuc.modules.admin.dto.DashboardStats;
import iuh.fit.se.tramcamxuc.modules.admin.service.AdminStatsService;
import iuh.fit.se.tramcamxuc.modules.music.artist.repository.ArtistRepository;
import iuh.fit.se.tramcamxuc.modules.music.genre.repository.GenreRepository;
import iuh.fit.se.tramcamxuc.modules.music.song.repository.SongRepository;
import iuh.fit.se.tramcamxuc.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminStatsServiceImpl implements AdminStatsService {

    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final MongoTemplate mongoTemplate;
    private final GenreRepository genreRepository;

    @Override
    public DashboardStats getOverview() {
        return DashboardStats.builder()
                .totalUsers(userRepository.count())
                .totalSongs(songRepository.count())
                .totalArtists(artistRepository.count())
                .totalListens(songRepository.getTotalListeningCount())
                .build();
    }

    @Override
    public List<ChartData> getListeningChart(int days) {
        LocalDateTime startDate = LocalDate.now().minusDays(days - 1).atStartOfDay();

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("listenedAt").gte(startDate)),
                Aggregation.project()
                        .andExpression("dateToString('%Y-%m-%d', listenedAt)").as("dateStr"),
                Aggregation.group("dateStr").count().as("count"),
                Aggregation.sort(Sort.Direction.ASC, "_id")
        );

        List<Map> results = mongoTemplate.aggregate(aggregation, "listening_history", Map.class).getMappedResults();

        Map<String, Long> dataMap = results.stream()
                .collect(Collectors.toMap(
                        m -> (String) m.get("_id"),
                        m -> ((Number) m.get("count")).longValue()
                ));

        List<ChartData> chartData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < days; i++) {
            String dateLabel = startDate.plusDays(i).format(formatter);
            chartData.add(new ChartData(dateLabel, dataMap.getOrDefault(dateLabel, 0L)));
        }

        return chartData;
    }

    @Override
    public List<ChartData> getGenreDistribution() {
        return genreRepository.getGenreSongCount();
    }
}