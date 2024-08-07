package io.onedev.server.model;

import io.onedev.server.model.support.BuildMetric;
import io.onedev.server.util.MetricIndicator;
import io.onedev.server.web.component.chart.line.LineSeries;

import javax.persistence.*;

import static io.onedev.server.model.support.BuildMetric.PROP_REPORT;

@Entity
@Table(
		indexes={@Index(columnList="o_build_id"), @Index(columnList= PROP_REPORT)},
		uniqueConstraints={@UniqueConstraint(columnNames={"o_build_id", PROP_REPORT})}
)
public class UnitTestMetric extends AbstractEntity implements BuildMetric {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	private Build build;
	
	@Column(nullable=false)
	private String reportName;
	
	private int testSuiteSuccessRate;
	
	private int testCaseSuccessRate;
	
	private int totalTestDuration;
	
	private int numOfTestSuites;

	private int numOfTestCases;
	
	@Override
	public Build getBuild() {
		return build;
	}

	public void setBuild(Build build) {
		this.build = build;
	}
	
	@Override
	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	@MetricIndicator(order=100, group="Success Rate", name="Test Suite", color="#1BC5BD",
			valueFormatter=LineSeries.PERCENTAGE_FORMATTER, maxValue=100, minValue=0)
	public int getTestSuiteSuccessRate() {
		return testSuiteSuccessRate;
	}

	public void setTestSuiteSuccessRate(int testSuiteSuccessRate) {
		this.testSuiteSuccessRate = testSuiteSuccessRate;
	}

	@MetricIndicator(order=200, group="Success Rate", name="Test Case", color="#F64E60",
			valueFormatter=LineSeries.PERCENTAGE_FORMATTER, maxValue=100, minValue=0)
	public int getTestCaseSuccessRate() {
		return testCaseSuccessRate;
	}

	public void setTestCaseSuccessRate(int testCaseSuccessRate) {
		this.testCaseSuccessRate = testCaseSuccessRate;
	}

	@MetricIndicator(order=400, group="Total Number", name="Test Suite", color="#1BC5BD")
	public int getNumOfTestSuites() {
		return numOfTestSuites;
	}

	public void setNumOfTestSuites(int numOfTestSuites) {
		this.numOfTestSuites = numOfTestSuites;
	}

	@MetricIndicator(order=500, group="Total Number", name="Test Case", color="#F64E60")
	public int getNumOfTestCases() {
		return numOfTestCases;
	}

	public void setNumOfTestCases(int numOfTestCases) {
		this.numOfTestCases = numOfTestCases;
	}
	
	@MetricIndicator(order=600, group = "Total Test Duration", valueFormatter= LineSeries.SECONDS_FORMATTER)
	public int getTotalTestDuration() {
		return totalTestDuration;
	}

	public void setTotalTestDuration(int totalTestDuration) {
		this.totalTestDuration = totalTestDuration;
	}

}
