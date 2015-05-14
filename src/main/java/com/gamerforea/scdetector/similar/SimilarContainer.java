package com.gamerforea.scdetector.similar;

import java.util.ArrayList;
import java.util.List;

import com.gamerforea.scdetector.similar.SimilarData.GsonSimilarData;

public class SimilarContainer
{
	public final ClassContainer classContainer;
	public List<SimilarData> similarClasses = new ArrayList<>();

	public SimilarContainer(ClassContainer classContainer)
	{
		this.classContainer = classContainer;
	}

	public SimilarData addSimilarClass(ClassContainer similarClassContainer)
	{
		SimilarData data = new SimilarData(similarClassContainer);
		this.similarClasses.add(data);
		return data;
	}

	public GsonSimilarContainer toGson()
	{
		return new GsonSimilarContainer(this);
	}

	public static class GsonSimilarContainer
	{
		public final String name;
		public final List<GsonSimilarData> similars = new ArrayList<>();

		public GsonSimilarContainer(SimilarContainer container)
		{
			this.name = container.classContainer.name;
			container.similarClasses.forEach((c) -> this.similars.add(c.toGson()));
		}
	}
}