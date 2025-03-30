package com.manageleaguefootball.demo.service.impl;

import com.manageleaguefootball.demo.dto.Info.TeamPageInfo;
import com.manageleaguefootball.demo.dto.TeamDTO;
import com.manageleaguefootball.demo.model.Schedule;
import com.manageleaguefootball.demo.model.Season;
import com.manageleaguefootball.demo.model.Team;
import com.manageleaguefootball.demo.repository.ScheduleRepository;
import com.manageleaguefootball.demo.repository.SeasonRepository;
import com.manageleaguefootball.demo.repository.TeamRepository;
import com.manageleaguefootball.demo.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final SeasonRepository seasonRepository;
    private final ScheduleRepository scheduleRepository; // đảm bảo bạn đã định nghĩa phương thức tìm schedule theo idSeason

    public static ModelMapper mapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    public static TeamDTO mapToView(Team team) {
        if (team == null) {
            return null;
        }
        return mapper().map(team, TeamDTO.class);
    }

    public static List<TeamDTO> mapToView(List<Team> teams) {
        return teams.stream()
                .map(TeamServiceImpl::mapToView)
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamDTO> findAllTeam() {
        return mapToView(teamRepository.findAll());
    }

    @Override
    public TeamDTO createTeam(TeamDTO model) {
        Season season = seasonRepository.findById(model.getIdSeason()).orElse(null);
        if (season == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Season id not found");
        }
        Team team = mapper().map(model, Team.class);
        return mapToView(teamRepository.save(team));
    }

    @Override
    public TeamDTO updateTeam(TeamDTO model) {
        Team team = teamRepository.findById(model.getId()).orElse(null);
        if (team == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team id not found");
        }
        mapper().map(model, team);
        return mapToView(teamRepository.save(team));
    }

    @Override
    public List<TeamDTO> findTeamByIdSeason(String id) {
        List<Team> teams = teamRepository.findAllByIdSeason(id);
        return mapToView(teams);
    }

    @Override
    public TeamDTO deleteTeam(String id) {
        Team team = teamRepository.findById(id).orElse(null);
        if (team == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team id not found");
        }
        teamRepository.delete(team);
        return mapToView(team);
    }

    @Override
    public List<TeamDTO> findTeamByOrderScore(String idSeason) {
        // Tính lại bảng xếp hạng cho mùa giải trước khi sắp xếp
        recalcTeamRanking(idSeason);

        List<Team> teams = teamRepository.findAllByIdSeason(idSeason);
        teams.sort(Comparator.comparingInt(Team::getScore)
                .thenComparingInt(team -> team.getGoalWin() - team.getGoalLoss())
                .reversed());

        return mapToView(teams);
    }

    @Override
    public List<TeamDTO> search(String id, TeamPageInfo model) {
        return mapToView(teamRepository.search(id, model));
    }

    @Override
    public Long count(String id, TeamPageInfo model) {
        return teamRepository.count(id, model);
    }

    @Override
    public List<TeamDTO> searchTeam(TeamPageInfo model) {
        return mapToView(teamRepository.searchT(model));
    }

    @Override
    public Long countTeam(TeamPageInfo model) {
        return teamRepository.countT(model);
    }

    // Phương thức tính lại bảng xếp hạng dựa trên kết quả của các trận đấu
    private void recalcTeamRanking(String seasonId) {
        // Lấy danh sách đội và đặt lại các chỉ số về 0
        List<Team> teams = teamRepository.findAllByIdSeason(seasonId);
        teams.forEach(team -> {
            team.setScore(0);
            team.setWin(0);
            team.setLoss(0);
            team.setDraw(0);
            team.setGoalWin(0);
            team.setGoalLoss(0);
            team.setDifference(0);
        });

        // Lấy danh sách các trận đấu của mùa giải
        List<Schedule> schedules = scheduleRepository.findAllByIdSeason(seasonId);
        if (schedules != null) {
            for (Schedule schedule : schedules) {
                // Chỉ tính các trận đấu đã được cập nhật kết quả (status true)
                if (schedule.isStatus()) {
                    int homeScore = schedule.getHomeScore();
                    int awayScore = schedule.getAwayScore();

                    // Tìm đội theo tên (đảm bảo tên được dùng trong schedule trùng khớp với tên của team)
                    Team teamHome = teams.stream()
                            .filter(t -> t.getName().equals(schedule.getTeamHome()))
                            .findFirst().orElse(null);
                    Team teamAway = teams.stream()
                            .filter(t -> t.getName().equals(schedule.getTeamAway()))
                            .findFirst().orElse(null);

                    if (teamHome != null && teamAway != null) {
                        if (homeScore > awayScore) {
                            teamHome.setScore(teamHome.getScore() + 3);
                            teamHome.setWin(teamHome.getWin() + 1);
                            teamHome.setGoalWin(teamHome.getGoalWin() + homeScore);
                            teamHome.setGoalLoss(teamHome.getGoalLoss() + awayScore);
                            teamHome.setDifference(teamHome.getGoalWin() - teamHome.getGoalLoss());

                            teamAway.setLoss(teamAway.getLoss() + 1);
                            teamAway.setGoalWin(teamAway.getGoalWin() + awayScore);
                            teamAway.setGoalLoss(teamAway.getGoalLoss() + homeScore);
                            teamAway.setDifference(teamAway.getGoalWin() - teamAway.getGoalLoss());
                        } else if (homeScore < awayScore) {
                            teamAway.setScore(teamAway.getScore() + 3);
                            teamAway.setWin(teamAway.getWin() + 1);
                            teamAway.setGoalWin(teamAway.getGoalWin() + awayScore);
                            teamAway.setGoalLoss(teamAway.getGoalLoss() + homeScore);
                            teamAway.setDifference(teamAway.getGoalWin() - teamAway.getGoalLoss());

                            teamHome.setLoss(teamHome.getLoss() + 1);
                            teamHome.setGoalWin(teamHome.getGoalWin() + homeScore);
                            teamHome.setGoalLoss(teamHome.getGoalLoss() + awayScore);
                            teamHome.setDifference(teamHome.getGoalWin() - teamHome.getGoalLoss());
                        } else { // Hòa
                            teamHome.setScore(teamHome.getScore() + 1);
                            teamAway.setScore(teamAway.getScore() + 1);
                            teamHome.setDraw(teamHome.getDraw() + 1);
                            teamAway.setDraw(teamAway.getDraw() + 1);
                            teamHome.setGoalWin(teamHome.getGoalWin() + homeScore);
                            teamHome.setGoalLoss(teamHome.getGoalLoss() + awayScore);
                            teamAway.setGoalWin(teamAway.getGoalWin() + awayScore);
                            teamAway.setGoalLoss(teamAway.getGoalLoss() + homeScore);
                            teamHome.setDifference(teamHome.getGoalWin() - teamHome.getGoalLoss());
                            teamAway.setDifference(teamAway.getGoalWin() - teamAway.getGoalLoss());
                        }
                    }
                }
            }
        }
        // Lưu lại các đội đã được tính toán lại
        teamRepository.saveAll(teams);
    }
}
