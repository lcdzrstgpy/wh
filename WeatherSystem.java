package org.sang.bean;

import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * 天气系统主类
 */
public class WeatherSystem {
    public static void main(String[] args) {
        WeatherStation station = new WeatherStation("Central Weather Station");

        // 添加传感器
        station.addSensor(new TemperatureSensor());
        station.addSensor(new HumiditySensor());
        station.addSensor(new PressureSensor());
        station.addSensor(new WindSensor());

        // 收集一周的天气数据
        for (int i = 0; i < 7; i++) {
            station.collectWeatherData();
            System.out.println("Day " + (i + 1) + " data collected.");
        }

        // 显示天气数据
        WeatherDisplay display = new WeatherDisplay(station);
        display.showCurrentWeather();
        display.showWeatherTrends();

        // 天气预报
        WeatherForecaster forecaster = new WeatherForecaster(station);
        WeatherForecast forecast = forecaster.predict(3);
        forecast.display();
    }
}

/**
 * 天气数据记录类
 */
class WeatherData {
    private LocalDateTime timestamp;
    private double temperature; // 摄氏度
    private double humidity;    // 百分比
    private double pressure;    // hPa
    private double windSpeed;   // km/h
    private String windDirection;

    public WeatherData(LocalDateTime timestamp, double temperature,
                       double humidity, double pressure,
                       double windSpeed, String windDirection) {
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
    }

    // Getters
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getTemperature() { return temperature; }
    public double getHumidity() { return humidity; }
    public double getPressure() { return pressure; }
    public double getWindSpeed() { return windSpeed; }
    public String getWindDirection() { return windDirection; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("Time: %s, Temp: %.1f°C, Humidity: %.1f%%, Pressure: %.1fhPa, Wind: %.1fkm/h %s",
                timestamp.format(formatter), temperature, humidity, pressure, windSpeed, windDirection);
    }
}

/**
 * 传感器接口
 */
interface Sensor {
    double readValue();
    String getUnit();
    String getType();
}

/**
 * 温度传感器
 */
class TemperatureSensor implements Sensor {
    private Random random = new Random();

    @Override
    public double readValue() {
        // 模拟温度读数，范围-10到40°C
        return -10 + random.nextDouble() * 50;
    }

    @Override
    public String getUnit() { return "°C"; }

    @Override
    public String getType() { return "Temperature"; }
}

/**
 * 湿度传感器
 */
class HumiditySensor implements Sensor {
    private Random random = new Random();

    @Override
    public double readValue() {
        // 模拟湿度读数，范围30%到100%
        return 30 + random.nextDouble() * 70;
    }

    @Override
    public String getUnit() { return "%"; }

    @Override
    public String getType() { return "Humidity"; }
}

/**
 * 气压传感器
 */
class PressureSensor implements Sensor {
    private Random random = new Random();

    @Override
    public double readValue() {
        // 模拟气压读数，范围950到1050 hPa
        return 950 + random.nextDouble() * 100;
    }

    @Override
    public String getUnit() { return "hPa"; }

    @Override
    public String getType() { return "Pressure"; }
}

/**
 * 风速传感器
 */
class WindSensor implements Sensor {
    private Random random = new Random();
    private String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};

    @Override
    public double readValue() {
        // 模拟风速读数，范围0到100 km/h
        return random.nextDouble() * 100;
    }

    public String readDirection() {
        // 随机选择一个风向
        return directions[random.nextInt(directions.length)];
    }

    @Override
    public String getUnit() { return "km/h"; }

    @Override
    public String getType() { return "Wind"; }
}

/**
 * 气象站类
 */
class WeatherStation {
    private String name;
    private List<Sensor> sensors;
    private List<WeatherData> historicalData;

    public WeatherStation(String name) {
        this.name = name;
        this.sensors = new ArrayList<>();
        this.historicalData = new ArrayList<>();
    }

    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
    }

    public void collectWeatherData() {
        double temp = 0, humidity = 0, pressure = 0, windSpeed = 0;
        String windDirection = "N/A";

        for (Sensor sensor : sensors) {
            switch (sensor.getType()) {
                case "Temperature":
                    temp = sensor.readValue();
                    break;
                case "Humidity":
                    humidity = sensor.readValue();
                    break;
                case "Pressure":
                    pressure = sensor.readValue();
                    break;
                case "Wind":
                    windSpeed = sensor.readValue();
                    if (sensor instanceof WindSensor) {
                        windDirection = ((WindSensor)sensor).readDirection();
                    }
                    break;
            }
        }

        WeatherData data = new WeatherData(LocalDateTime.now(), temp, humidity, pressure, windSpeed, windDirection);
        historicalData.add(data);
    }

    public List<WeatherData> getHistoricalData() {
        return new ArrayList<>(historicalData);
    }

    public WeatherData getLatestData() {
        if (historicalData.isEmpty()) {
            return null;
        }
        return historicalData.get(historicalData.size() - 1);
    }

    public String getName() {
        return name;
    }
}

/**
 * 天气显示类
 */
class WeatherDisplay {
    private WeatherStation station;

    public WeatherDisplay(WeatherStation station) {
        this.station = station;
    }

    public void showCurrentWeather() {
        WeatherData data = station.getLatestData();
        if (data == null) {
            System.out.println("No weather data available.");
            return;
        }

        System.out.println("\n=== Current Weather ===");
        System.out.println("Station: " + station.getName());
        System.out.println(data);
        System.out.println("Weather condition: " + getWeatherCondition(data));
        System.out.println("======================\n");
    }

    public void showWeatherTrends() {
        List<WeatherData> data = station.getHistoricalData();
        if (data.isEmpty()) {
            System.out.println("No historical data available.");
            return;
        }

        System.out.println("\n=== Weather Trends ===");
        System.out.println("Station: " + station.getName());

        // 显示温度趋势
        System.out.println("\nTemperature Trend:");
        for (WeatherData d : data) {
            System.out.printf("%s: %.1f°C\n",
                    d.getTimestamp().format(DateTimeFormatter.ofPattern("MM-dd")),
                    d.getTemperature());
        }

        // 显示湿度趋势
        System.out.println("\nHumidity Trend:");
        for (WeatherData d : data) {
            System.out.printf("%s: %.1f%%\n",
                    d.getTimestamp().format(DateTimeFormatter.ofPattern("MM-dd")),
                    d.getHumidity());
        }

        System.out.println("=====================\n");
    }

    private String getWeatherCondition(WeatherData data) {
        if (data.getTemperature() < 0) {
            if (data.getHumidity() > 70) {
                return "Snow";
            }
            return "Freezing";
        } else if (data.getTemperature() < 10) {
            if (data.getHumidity() > 80) {
                return "Rain";
            }
            return "Cloudy";
        } else if (data.getTemperature() < 25) {
            if (data.getHumidity() > 70) {
                return "Showers";
            }
            return "Partly Cloudy";
        } else {
            if (data.getHumidity() > 60) {
                return "Thunderstorm possible";
            }
            return "Sunny";
        }
    }
}

/**
 * 天气预报类
 */
class WeatherForecast {
    private List<WeatherPrediction> predictions;

    public WeatherForecast() {
        this.predictions = new ArrayList<>();
    }

    public void addPrediction(WeatherPrediction prediction) {
        predictions.add(prediction);
    }

    public void display() {
        System.out.println("\n=== Weather Forecast ===");
        for (WeatherPrediction prediction : predictions) {
            System.out.printf("Day %d: %s, Temp: %.1f to %.1f°C, %s\n",
                    prediction.getDay(),
                    prediction.getCondition(),
                    prediction.getMinTemp(),
                    prediction.getMaxTemp(),
                    prediction.getDescription());
        }
        System.out.println("=======================\n");
    }
}

/**
 * 天气预报条目
 */
class WeatherPrediction {
    private int day;
    private String condition;
    private double minTemp;
    private double maxTemp;
    private String description;

    public WeatherPrediction(int day, String condition, double minTemp, double maxTemp, String description) {
        this.day = day;
        this.condition = condition;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.description = description;
    }

    // Getters
    public int getDay() { return day; }
    public String getCondition() { return condition; }
    public double getMinTemp() { return minTemp; }
    public double getMaxTemp() { return maxTemp; }
    public String getDescription() { return description; }
}

/**
 * 天气预报器
 */
class WeatherForecaster {
    private WeatherStation station;

    public WeatherForecaster(WeatherStation station) {
        this.station = station;
    }

    public WeatherForecast predict(int days) {
        List<WeatherData> historicalData = station.getHistoricalData();
        if (historicalData.isEmpty()) {
            throw new IllegalStateException("No historical data available for prediction");
        }

        WeatherForecast forecast = new WeatherForecast();

        // 简单预测模型 - 实际应用中会使用更复杂的算法
        double lastTemp = historicalData.get(historicalData.size() - 1).getTemperature();
        double lastHumidity = historicalData.get(historicalData.size() - 1).getHumidity();
        double lastPressure = historicalData.get(historicalData.size() - 1).getPressure();

        Random random = new Random();

        for (int i = 1; i <= days; i++) {
            // 模拟天气变化
            double tempChange = (random.nextDouble() - 0.5) * 5;
            double newTemp = lastTemp + tempChange;

            double humidityChange = (random.nextDouble() - 0.5) * 10;
            double newHumidity = Math.max(30, Math.min(100, lastHumidity + humidityChange));

            double pressureChange = (random.nextDouble() - 0.5) * 5;
            double newPressure = Math.max(950, Math.min(1050, lastPressure + pressureChange));

            // 确定天气状况
            String condition = determineCondition(newTemp, newHumidity, newPressure);
            String description = generateDescription(condition, newTemp);

            // 温度范围
            double minTemp = newTemp - 2 + random.nextDouble() * 4;
            double maxTemp = newTemp + 2 + random.nextDouble() * 4;

            forecast.addPrediction(new WeatherPrediction(
                    i, condition, minTemp, maxTemp, description
            ));

            // 更新最后的值
            lastTemp = newTemp;
            lastHumidity = newHumidity;
            lastPressure = newPressure;
        }

        return forecast;
    }

    private String determineCondition(double temp, double humidity, double pressure) {
        if (temp < 0) {
            if (humidity > 70) return "Snow";
            return "Freezing";
        } else if (temp < 10) {
            if (humidity > 80) return "Rain";
            if (pressure < 1000) return "Cloudy";
            return "Partly Cloudy";
        } else if (temp < 25) {
            if (humidity > 70 && pressure < 1010) return "Showers";
            return "Partly Cloudy";
        } else {
            if (humidity > 60 && pressure < 1005) return "Thunderstorm possible";
            return "Sunny";
        }
    }

    private String generateDescription(String condition, double temp) {
        switch (condition) {
            case "Snow":
                return "Heavy snowfall expected, travel may be affected";
            case "Freezing":
                return "Freezing temperatures, risk of ice on roads";
            case "Rain":
                return "Persistent rain throughout the day";
            case "Cloudy":
                return "Overcast with little sunshine expected";
            case "Showers":
                return "Scattered showers throughout the day";
            case "Partly Cloudy":
                return "Mix of sun and clouds";
            case "Thunderstorm possible":
                return "Hot and humid with chance of thunderstorms";
            case "Sunny":
                if (temp > 30) {
                    return "Extremely hot, stay hydrated";
                }
                return "Clear skies and sunny";
            default:
                return "Weather conditions normal for this time of year";
        }
    }
}

/**
 * 天气警报系统
 */
class WeatherAlertSystem {
    private List<WeatherAlert> activeAlerts;

    public WeatherAlertSystem() {
        this.activeAlerts = new ArrayList<>();
    }

    public void checkForAlerts(WeatherData data) {
        // 检查极端温度
        if (data.getTemperature() < -15) {
            issueAlert("Extreme Cold Warning",
                    "Dangerously cold temperatures expected",
                    data.getTimestamp());
        } else if (data.getTemperature() > 35) {
            issueAlert("Heat Warning",
                    "Extreme heat expected, risk of heat stroke",
                    data.getTimestamp());
        }

        // 检查高风速
        if (data.getWindSpeed() > 60) {
            issueAlert("High Wind Warning",
                    "Damaging winds expected, secure loose objects",
                    data.getTimestamp());
        }

        // 检查低气压 (可能的风暴)
        if (data.getPressure() < 980) {
            issueAlert("Storm Warning",
                    "Low pressure system detected, possible storm approaching",
                    data.getTimestamp());
        }
    }

    private void issueAlert(String type, String message, LocalDateTime time) {
        WeatherAlert alert = new WeatherAlert(type, message, time);
        activeAlerts.add(alert);
        System.out.println("\n!!! WEATHER ALERT !!!");
        System.out.println(alert);
        System.out.println("!!! TAKE NECESSARY PRECAUTIONS !!!\n");
    }

    public List<WeatherAlert> getActiveAlerts() {
        return new ArrayList<>(activeAlerts);
    }
}

/**
 * 天气警报类
 */
class WeatherAlert {
    private String type;
    private String message;
    private LocalDateTime issueTime;
    private boolean expired;

    public WeatherAlert(String type, String message, LocalDateTime issueTime) {
        this.type = type;
        this.message = message;
        this.issueTime = issueTime;
        this.expired = false;
    }

    public void expire() {
        this.expired = true;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("[%s] %s - %s (Issued: %s)",
                expired ? "EXPIRED" : "ACTIVE",
                type,
                message,
                issueTime.format(formatter));
    }
}

/**
 * 天气数据统计分析器
 */
class WeatherAnalyzer {
    private WeatherStation station;

    public WeatherAnalyzer(WeatherStation station) {
        this.station = station;
    }

    public double getAverageTemperature() {
        List<WeatherData> data = station.getHistoricalData();
        if (data.isEmpty()) return 0;

        double sum = 0;
        for (WeatherData d : data) {
            sum += d.getTemperature();
        }
        return sum / data.size();
    }

    public double getMaxTemperature() {
        return station.getHistoricalData().stream()
                .mapToDouble(WeatherData::getTemperature)
                .max()
                .orElse(0);
    }

    public double getMinTemperature() {
        return station.getHistoricalData().stream()
                .mapToDouble(WeatherData::getTemperature)
                .min()
                .orElse(0);
    }

    public Map<String, Long> getWindDirectionDistribution() {
        Map<String, Long> distribution = new HashMap<>();
        for (WeatherData data : station.getHistoricalData()) {
            distribution.merge(data.getWindDirection(), 1L, Long::sum);
        }
        return distribution;
    }

    public void displayStatistics() {
        System.out.println("\n=== Weather Statistics ===");
        System.out.printf("Average Temperature: %.1f°C\n", getAverageTemperature());
        System.out.printf("Maximum Temperature: %.1f°C\n", getMaxTemperature());
        System.out.printf("Minimum Temperature: %.1f°C\n", getMinTemperature());

        System.out.println("\nWind Direction Distribution:");
        getWindDirectionDistribution().forEach((dir, count) -> {
            System.out.printf("%s: %d times\n", dir, count);
        });

        System.out.println("=========================\n");
    }
}