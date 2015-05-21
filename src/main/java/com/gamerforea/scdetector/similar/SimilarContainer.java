package com.gamerforea.scdetector.similar;

import java.util.ArrayList;
import java.util.List;

import com.gamerforea.scdetector.similar.ClassContainer.Type;
import com.gamerforea.scdetector.similar.SimilarData.GsonSimilarData;

public class SimilarContainer implements Comparable<SimilarContainer>
{
	public final ClassContainer classContainer;
	public List<SimilarData> similarClasses = new ArrayList<>();
	public boolean validated = false;

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

	@Override
	public String toString()
	{
		return this.classContainer.name + "[" + this.similarClasses.size() + "]";
	}

	@Override
	public int compareTo(SimilarContainer o)
	{
		return this.classContainer.name.compareTo(o.classContainer.name);
	}

	public static class GsonSimilarContainer
	{
		public final String name;
		public final Type type;
		public final boolean validated;
		public final List<GsonSimilarData> similars = new ArrayList<>();

		public GsonSimilarContainer(SimilarContainer container)
		{
			this.name = container.classContainer.name;
			this.type = container.classContainer.type;
			this.validated = container.validated;
			container.similarClasses.forEach((c) -> this.similars.add(c.toGson()));
		}
	}
}